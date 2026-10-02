# Ripperdoc: Taller de patrones (Grupo 12)

Clínica de implantes cibernéticos inspirada en Cyberpunk. Un paciente llega con un
*lifepath* (Street Kid, Nomad o Corpo) y el ripperdoc le instala implantes uno sobre otro:
cada implante envuelve al humano anterior y cambia sus estadísticas. El proyecto muestra
**cuatro patrones de diseño implementados a mano** (nada de patrones de framework):
Decorator, Prototype, Builder y Abstract Factory.

- **Backend:** Java 17 puro, sin frameworks ni dependencias (servidor HTTP del propio JDK).
- **Interfaz:** HTML + CSS + JS sin librerías, servida por el mismo servidor.
- **Código e identificadores en inglés; interfaz en español.**

## Demo en vivo

**Link funcional:** https://qjvm0n3jxk.execute-api.us-east-1.amazonaws.com

Desplegado en **AWS Lambda** (Java 21) y expuesto con **API Gateway HTTP API**, dentro de
la capa gratuita. Para correrlo en local, ver [Cómo correrlo](#cómo-correrlo).

---

## Caso de estudio

### El problema

Un ripperdoc instala combinaciones de implantes y cada implante cambia las estadísticas del
paciente (fuerza, reflejos, hackeo, armadura, humanidad) y su costo. Con herencia simple
haría falta una subclase por combinación (`NomadWithMantisBladesAndSandevistan`, ...):
8 implantes sobre 3 lifepaths son 3 × 2⁸ combinaciones posibles, y cada implante nuevo
duplica el número. Además dos implantes ocupan la misma zona del cuerpo, y hay implantes
que **multiplican** (Sandevistan ×1.5 reflejos) mientras otros **suman**. El orden de
instalación cambia el resultado.

### La solución (Decorator como patrón central)

Cada implante es un **decorador**: implementa `Human`, guarda un `Human` envuelto y delega,
añadiendo solo lo suyo. La cadena se arma en tiempo de ejecución:

```java
Human patient =
    new Sandevistan(
        new KerenzikovReflex(
            new Nomad("V")));

patient.getReflexes();
```

| Rol del patrón   | Clase en el proyecto                                                         |
|------------------|------------------------------------------------------------------------------|
| **Component**        | `model/Human`                                                                 |
| **Concrete Component** | `model/BaseHuman` + `StreetKid`, `Nomad`, `Corpo`                          |
| **Decorator**        | `decorator/ImplantDecorator`                                                  |
| **Concrete Decorators** | `MantisBlades`, `GorillaArms`, `Sandevistan`, `KerenzikovReflex`, `CyberdeckQuickhack`, `KiroshiOptics`, `SubdermalArmor`, `OpticalCamo`, `Berserk`, `ProjectileArms`, `TargetingOptics` |
| **Client**           | `service/Ripperdoc` y `builder/PatientBuilder`                                |

El costo y las estadísticas se calculan **hacia adentro de forma recursiva**: el decorador
externo pide el valor al interno y aplica su cambio cuando el valor regresa. Por eso el
orden importa:

| Cadena (de adentro hacia afuera)            | Reflejos de un Nomad |
|---------------------------------------------|----------------------|
| Nomad → Kerenzikov → Sandevistan            | (40 + 15) × 1.5 = **83** |
| Nomad → Sandevistan → Kerenzikov            | (40 × 1.5) + 15 = **75** |

La humanidad final define el estado (`model/Condition`): `STABLE` ≥ 50, `UNSTABLE` ≥ 20,
`CYBERPSYCHOSIS` < 20.

---

## Los cuatro patrones y dónde se implementaron

| Patrón | Qué aporta al caso | Clases / archivos |
|--------|--------------------|-------------------|
| **Decorator** | Los implantes envuelven al humano y suman/multiplican estadísticas y costo. | `model/Human`, `model/BaseHuman`, `model/{StreetKid,Nomad,Corpo}`, `decorator/ImplantDecorator`, `decorator/*` (11 implantes), `service/Ripperdoc` |
| **Prototype** | Clonar un build completo (cadena de decoradores) sin reconstruirlo, y precargar presets. | `model/Human#copy()`, `model/{StreetKid,Nomad,Corpo}#copy()`, `decorator/ImplantDecorator#copy()/recreate()`, `decorator/*#recreate()`, `prototype/PresetRegistry` |
| **Builder** | Armar paso a paso y validar el paciente (nombre, lifepath, implantes ordenados, zonas ocupadas). | `builder/PatientBuilder` |
| **Abstract Factory** | Familias de cyberware compatibles (Civilian y Military) que producen implantes relacionados por zona. | `factory/CyberwareFactory`, `factory/CivilianCyberwareFactory`, `factory/MilitaryCyberwareFactory`, `factory/CyberwareFactoryRegistry` |

Los cuatro se combinan: la **Abstract Factory** entrega un kit de implantes (armas,
sistema operativo, óptica); el **Builder** los ensambla en una cadena válida; cada
implante es un **Decorator**; y el **Prototype** permite clonar un preset o un build
completo. Ninguno usa decoradores ni fábricas de framework: todo es código propio.

---

## Cómo correrlo

Requisito: **JDK 17 o superior** (`java -version`, `javac -version`).

macOS / Linux:

```bash
./run.sh
```

Windows:

```bat
run.bat
```

Luego abre **http://localhost:8080**. Para usar otro puerto:

```bash
./run.sh 8081
```

A mano:

```bash
javac --release 17 -d out $(find src -name "*.java")
java --add-modules jdk.httpserver -cp out com.group12.ripperdoc.Main
```

> `com.sun.net.httpserver` vive en el módulo `jdk.httpserver`; por eso el `--add-modules`.

La interfaz recuerda el build en `localStorage` y admite **deep-links**, por ejemplo
`http://localhost:8080/?lifepath=nomad&implants=mantis-blades,sandevistan` o
`?preset=street-samurai`.

---

## API (`GET`)

| Ruta | Descripción |
|------|-------------|
| `/api/lifepaths` | Lifepaths con sus estadísticas base. |
| `/api/implants` | Catálogo de implantes (decoradores concretos). |
| `/api/families` | Familias de cyberware (Abstract Factory) y su kit. |
| `/api/presets` | Presets precargados para clonar (Prototype). |
| `/api/build?lifepath=nomad&implants=kerenzikov,sandevistan&family=military&name=V` | Arma el build y devuelve stats, condición, factura y una foto por cada capa. |
| `/api/build?preset=street-samurai` | Clona un preset (Prototype) y devuelve el resultado. |

Respuesta de `/api/build`:

```json
{
  "description": "V the Nomad + Kerenzikov + Sandevistan",
  "stats": { "strength": 50, "reflexes": 83, "hacking": 20, "armor": 30, "humanity": 65, "cost": 19000 },
  "condition": "STABLE",
  "thresholds": { "stable": 50, "psychosis": 20 },
  "layers": [ { "id": "nomad", "className": "Nomad", "stats": { } }, { "id": "kerenzikov", "className": "KerenzikovReflex", "stats": { } } ]
}
```

---

## Estructura

```
src/com/group12/ripperdoc/
├── Main.java                 Punto de entrada (arranca el servidor)
├── model/                    Component y Concrete Components (+ Prototype)
│   ├── Human.java, BaseHuman.java, StreetKid.java, Nomad.java, Corpo.java, Condition.java
├── decorator/                Decorator abstracto + 11 implantes
├── factory/                  Abstract Factory: familias de cyberware
├── builder/                  Builder: armado y validación del paciente
├── prototype/                Prototype: registro de presets clonables
├── service/                  Catálogo, implantes (fichas) y Ripperdoc
└── api/                      Servidor HTTP y JSON propios
web/                          index.html, styles.css, silhouette.js, app.js
```

Dependencias en una sola dirección: `api` usa `builder`, `factory`, `prototype` y
`service`; `service` usa `decorator` y `model`; `decorator` usa `model`.

---

## Despliegue (AWS Lambda)

Objetivo: publicar la app bajo una **Function URL** de AWS Lambda dentro de la **capa
gratuita** (sin API Gateway ni servidores 24/7).

**Qué falta para desplegar** (se necesita de tu lado):

1. **Credenciales de AWS** de una cuenta con permisos para crear Lambda, IAM y Function URL
   (Access Key ID + Secret Access Key, o un perfil ya configurado) y la **región** (p. ej.
   `us-east-1`). No hay nada de AWS configurado en este equipo todavía.
2. **AWS CLI v2** (lo instalo yo si no está) y **JDK 17+**.

Con eso, el plan es: empaquetar `out/` + `web/` + un handler Lambda que reusa
`ApiRouter`, subir el `.zip` a Lambda (`java21`), crear el rol de ejecución con la política
`AWSLambdaBasicExecutionRole`, crear la **Function URL** pública y pegar el enlace en la
sección [Demo en vivo](#demo-en-vivo).

---

## Reglas del código

- Código en inglés (clases, métodos, variables); interfaz en español.
- Sin comentarios ni emojis; los nombres se explican solos.
- Java 17 sin dependencias externas ni frameworks.
- Los patrones (incluidos los decoradores) están construidos a mano.

---

## Problemas comunes

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `Port 8080 is busy` | Otro proceso usa el puerto | `./run.sh 8081` |
| "sin servidor" en la interfaz | El servidor no está corriendo | `./run.sh` y entrar por `http://localhost:8080` |
| 404 en `/` | Se arrancó desde otra carpeta | Ejecutar desde la raíz del proyecto |
| `permission denied: ./run.sh` | Sin permiso de ejecución | `chmod +x run.sh` |
