
# AML Server

## DE - Deutsche Anleitung

### Installation

Dies ist ein Server für [AML](https://github.com/triploit/aml). Um den Server aufzusetzen, kann er erstmal mit [SecundoPM](https://github.com/triploit/secundo-pm) installiert werden:

```bash
sudo secpm install aml-server
```

### Server aufsetzen

Wenn du eine Website hast, die du komplett in AML geschrieben hast und einen eigenen Server dafür aufsetzen willst, musst du nicht Apache etc. verwenden und die ganzen Dateien kompilieren. Du kannst die direkten AML-Dateien (die, wenn sie als Referenz in Links wie `a (href: "test.html") { "Test" }` angegeben sind, um nach dem Kompilieren zu funktionieren, in `a ( href: "test.aml") { "Test" }` geändert werden müssen) verwenden.

Lege dazu einen Source-Ordner an, wie zum Beispiel:

```bash
$ mkdir /home/user/source/
```
Dort kommen dann alle AML-Dateien oder nötigen Bilder, Stylesheets etc. für die Website rein. Weiterhin muss der Server dann wissen, auf welchem Port er laufen soll und wo die Dateien (wie (**wichtig**) die `index.aml`, die standardmäßig geladen wird, wie die index.html auf normalen Servern) liegen.
Daher braucht der Server eine JSON-Datei, wo dies beschrieben ist:

```json
{
	"port": "80",
	"source_dir": "/home/user/source"
}
``` 

Diese muss man dann beim Starten angeben:
```bash
$ aml_server settings.json
```
Wichtig ist beim Port 80, dass der Server dann als root gestartet werden muss. Bei Ports, die über 2000 sind, ist dies aber nicht nötig.

## ENG - English manual
### Installation

This is a server for [AML](https://github.com/triploit/aml). To set up this server, you can install it with [SecundoPM](https://github.com/triploit/secundo-pm):

```bash
sudo secpm install aml-server
```

### Set up the server

If you have a website, which is completely written in AML and want to set up a server for it, you don't have to use Apache etc. and you don't have to compile the AML files. You can use the direct AML files.

Create for this a new folder:

```bash
$ mkdir /home/user/source/
```
There you can put the AML files, images, stylesheets, etc. But the server have to know which port he can use and where he can find the source file (like the **`index.aml`**).
For this the server needs a JSON file:

```json
{
	"port": "80",
	"source_dir": "/home/user/source"
}
``` 

This have to be passed as an argument:

```bash
$ aml_server settings.json
```

It's important that, if you use the port 80, that `aml_server` must be run as root.
