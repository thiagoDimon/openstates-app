@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements. See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership. The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License. You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied. See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_SAVE_ERRORLEVEL__=
@SET __MVNW_SAVE_CD__=%CD%
@cd /D "%~dp0"

@SET __MVNW_WRAPPER_JAR__=%~dp0.mvn\wrapper\maven-wrapper.jar
@SET __MVNW_WRAPPER_JAR_VALID__=

@IF EXIST "%__MVNW_WRAPPER_JAR__%"  (
  @SET "__MVNW_WRAPPER_JAR_VALID__=true"
) ELSE (
  @ECHO Downloading Maven Wrapper...
  @powershell -Command "$wrapperUrl = 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar'; Invoke-WebRequest -Uri $wrapperUrl -OutFile '%__MVNW_WRAPPER_JAR__%'"
  @IF EXIST "%__MVNW_WRAPPER_JAR__%"  (
    @SET "__MVNW_WRAPPER_JAR_VALID__=true"
  )
)

@IF NOT "%__MVNW_WRAPPER_JAR_VALID__%"=="true" (
  @ECHO Failed to download Maven Wrapper. Please download it manually from:
  @ECHO https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
  @ECHO and place it in the .mvn\wrapper directory.
  @EXIT /B 1
)

@SET MAVEN_PROJECTBASEDIR=%~dp0

@FOR /F "usebackq delims=" %%a IN ("%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties") DO @(
  @SET "line=%%a"
  @IF "!line:~0,16!"=="distributionUrl" (
    @SET "MVNW_DIST_URL=!line:~17!"
  )
)

@"%JAVA_HOME%\bin\java.exe" ^
  %MAVEN_OPTS% ^
  -classpath "%__MVNW_WRAPPER_JAR__%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CONFIG% %*

@SET __MVNW_SAVE_ERRORLEVEL__=%ERRORLEVEL%
@cd /D "%__MVNW_SAVE_CD__%"
@EXIT /B %__MVNW_SAVE_ERRORLEVEL__%
