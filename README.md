# scalajs-java-securerandom

`scalajs-java-securerandom` provides `java.security.SecureRandom` for Scala.js, in Node.js, in browsers, and in other environments that provide the Web Crypto API.

## Usage

Use the following dependency:

```scala
libraryDependencies += ("org.scala-js" %%% "scalajs-java-securerandom" % "1.0.0").cross(CrossVersion.for3Use2_13)
```

When using a `crossProject`, add the above in `.jsSettings(...)`.

You can then use `java.security.SecureRandom` from your code, and by extension, the `java.util.UUID.randomUUID()` method.

When running in an unsupported environment, a `java.lang.UnsupportedOperationException` will be thrown when trying to instantiate `java.security.SecureRandom`.

Supported environments are:

* Node.js,
* Browsers,
* Using [JSDOM](https://github.com/jsdom/jsdom) >= 20.0.0
* Other environments that provide `crypto.getRandomValues(typedArray)`, from the Web Crypto API.

## License

`scalajs-java-securerandom` is distributed under the [Apache 2.0 license](./LICENSE.txt), like Scala.js itself.
