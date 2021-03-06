package com.contrastsecurity.maven.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.junit.Assert.*;

public class InstallAgentContrastMavenMojoTest {
    InstallAgentContrastMavenMojo installMojo;
    Date now;

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Before
    public void setUp() {
        installMojo = new InstallAgentContrastMavenMojo();
        installMojo.appName = "caddyshack";
        installMojo.serverName = "Bushwood";
        installMojo.contrastAgentLocation = "/usr/local/bin/contrast.jar";

        now = new Date();
        environmentVariables.clear("TRAVIS_BUILD_NUMBER", "CIRCLE_BUILD_NUM");
    }

   @Test
   public void testGenerateAppVersion() {
        installMojo.appVersion = "mycustomversion";
        installMojo.computedAppVersion = null;
        assertEquals("mycustomversion",installMojo.computeAppVersion(now));
   }

    @Test
    public void testGenerateAppVersionNoAppVersion() {
        installMojo.appVersion = null;
        installMojo.computedAppVersion = null;
        String expectedVersion = new SimpleDateFormat("yyyyMMddHHmmss").format(now);
        assertEquals("caddyshack-" + expectedVersion,installMojo.computeAppVersion(now));
        assertEquals("caddyshack-" + expectedVersion,installMojo.computeAppVersion(now));
    }

    @Test
    public void testGenerateAppVersionTravis() {
        installMojo.appVersion = null;
        installMojo.computedAppVersion = null;
        environmentVariables.set("TRAVIS_BUILD_NUMBER", "19");
        assertEquals("caddyshack-19",installMojo.computeAppVersion(now));
        assertEquals("caddyshack-19",installMojo.computeAppVersion(now));
    }

    @Test
    public void testGenerateAppVersionCircle() {
        installMojo.appVersion = null;
        installMojo.computedAppVersion = null;
        environmentVariables.set("TRAVIS_BUILD_NUMBER", "circle");
        assertEquals("caddyshack-circle",installMojo.computeAppVersion(now));
        assertEquals("caddyshack-circle",installMojo.computeAppVersion(now));
    }

    @Test
    public void testBuildArgLine() {
        installMojo.computedAppVersion = "caddyshack-2";
        String currentArgLine = "";
        String expectedArgLine = "-javaagent:/usr/local/bin/contrast.jar -Dcontrast.server=Bushwood -Dcontrast.env=qa -Dcontrast.override.appversion=caddyshack-2 -Dcontrast.reporting.period=200 -Dcontrast.override.appname=caddyshack";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));

        installMojo.serverPath = "/home/tomcat/app/";
        currentArgLine = "";
        expectedArgLine = "-javaagent:/usr/local/bin/contrast.jar -Dcontrast.server=Bushwood -Dcontrast.env=qa -Dcontrast.override.appversion=caddyshack-2 -Dcontrast.reporting.period=200 -Dcontrast.override.appname=caddyshack -Dcontrast.path=/home/tomcat/app/";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));

        installMojo.standalone = true;
        expectedArgLine = "-javaagent:/usr/local/bin/contrast.jar -Dcontrast.server=Bushwood -Dcontrast.env=qa -Dcontrast.override.appversion=caddyshack-2 -Dcontrast.reporting.period=200 -Dcontrast.standalone.appname=caddyshack -Dcontrast.path=/home/tomcat/app/";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));
    }

    @Test
    public void testBuildArgNull() {
        installMojo.computedAppVersion = "caddyshack-2";
        String currentArgLine = null;
        String expectedArgLine = "-javaagent:/usr/local/bin/contrast.jar -Dcontrast.server=Bushwood -Dcontrast.env=qa -Dcontrast.override.appversion=caddyshack-2 -Dcontrast.reporting.period=200 -Dcontrast.override.appname=caddyshack";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));
    }

    @Test
    public void testBuildArgLineAppend() {
        installMojo.computedAppVersion = "caddyshack-2";
        String currentArgLine = "-Xmx1024m";
        String expectedArgLine = "-Xmx1024m -javaagent:/usr/local/bin/contrast.jar -Dcontrast.server=Bushwood -Dcontrast.env=qa -Dcontrast.override.appversion=caddyshack-2 -Dcontrast.reporting.period=200 -Dcontrast.override.appname=caddyshack";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));
    }

    @Test
    public void testBuildArgLineSkip() {
        installMojo.skipArgLine = true;
        String currentArgLine = "-Xmx1024m";
        String expectedArgLine = "-Xmx1024m";
        assertEquals(expectedArgLine, installMojo.buildArgLine(currentArgLine));
    }
}
