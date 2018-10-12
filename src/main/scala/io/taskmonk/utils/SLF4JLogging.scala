package io.taskmonk.utils

import org.slf4j.LoggerFactory

trait SLF4JLogging {
  @transient
  lazy val log = LoggerFactory.getLogger(this.getClass.getName)
}

