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

package org.scalajs.testsuite.javalib.util

import org.junit.Test
import org.junit.Assert._
import org.junit.Assume._

import java.util.UUID

import org.scalajs.testsuite.utils.Platform

class UUIDTest {
  @Test def randomUUID(): Unit = {
    assumeTrue("requires a supported CSPRNG", Platform.hasSupportedCSPRNG)

    val uuid1 = UUID.randomUUID()
    assertEquals(2, uuid1.variant())
    assertEquals(4, uuid1.version())

    val uuid2 = UUID.randomUUID()
    assertEquals(2, uuid2.variant())
    assertEquals(4, uuid2.version())

    assertNotEquals(uuid1, uuid2)
  }

  @Test def verifyFailure(): Unit = {
    assumeFalse("requires that there is no supported CSPRNG", Platform.hasSupportedCSPRNG)

    val gotFailure = try {
      UUID.randomUUID()
      false
    } catch {
      case _: UnsupportedOperationException => true
    }

    assertTrue("expected a failure", gotFailure)
  }
}
