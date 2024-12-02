# Voting Table Query System 🗳️

![ICESI University Logo](https://upload.wikimedia.org/wikipedia/commons/d/d6/Logo_de_la_Universidad_ICESI.svg)

## Authors ✒️

- Esteban Gaviria Zambrano - A00396019
- Miguel Angel Gonzalez Arango - A00395687
- Juan David Colonia Aldana - A00395956
- Juan Manuel Diaz Moreno - A00394477

---

## Project Overview 📄

The objective of this project is to compare and analyze the behavior of a program to perform concurrent queries through experimentation. In addition to implementing the following patterns:

1. **Observer**
2. **ThreadPool**
3. **Master-Worker**
4. **Broker**

The distributed version of the program makes use of these patterns to improve some quality attributes such as availability. It is then compared to the monolithic version of the program.

## Application Execution 🚀

1. Open a terminal and access the `deployment-broker` folder.

```bash
cd deployment-broker
```

2. Run the IceGrid Registry and Nodes. In case you need more nodes, add more `configuration.nodeX` in the `deployment.broker` folder:

```bash
icegridregistry --Ice.Config=config/registry.config
```

```bash
icegridnode --Ice.Config=config/NodeX.config
```

3. Enter to the IceGrid Admin: `icegridadmin` where you can use any username and an empty password, and add the application:

```bash
application add application-template.xml
```

If it already exists, then run:

```bash
application update application-template.xml`
```

4. Execute the clients jar as usual

```bash
gradle build
```

```bash
java -jar master/build/libs/master.jar
```

## Technologies Used 🛠️

<div style="text-align: left"> <p> <a href="https://www.java.com" target="_blank"> <img alt="Java" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original.svg" height="60" width="60"></a> <a href="https://zeroc.com/products/ice" target="_blank"> <img alt="ZeroIce" src="https://zeroc.com/images/ice-logo.svg" height="60" width="60"></a> <a href="https://gradle.org/" target="_blank"> <img alt="Gradle" src="https://gradle.com/_next/static/media/iconElephant.6b7923d8.svg" height="60" width="60"></a> </p> </div>
