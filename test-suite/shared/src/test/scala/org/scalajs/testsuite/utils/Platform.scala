/*
 * scalajs-java-securerandom (https://github.com/scala-js/scala-js-java-securerandom)
 *
 * Copyright EPFL.
 *
 * Licensed under Apache License 2.0
 * (https://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package org.scalajs.testsuite.utils

object Platform {
  val hasSupportedCSPRNG: Boolean =
    BuildInfo.jsEnvKind != "jsdomnodejs"
}
