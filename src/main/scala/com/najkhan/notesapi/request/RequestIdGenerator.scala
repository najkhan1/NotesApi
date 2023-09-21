package com.najkhan.notesapi.request

import java.util.UUID


object RequestIdGenerator {

  def generateRequestId = UUID.randomUUID().toString

}
