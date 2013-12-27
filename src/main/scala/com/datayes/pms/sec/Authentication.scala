package com.datayes.pms.sec

case class Authentication(name: String, password: String, roles: List[Sid])