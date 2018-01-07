/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.model.script.extension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.smarthome.io.console.Console;
import org.eclipse.smarthome.io.console.extensions.AbstractConsoleCommandExtension;
import org.eclipse.smarthome.model.script.engine.Script;
import org.eclipse.smarthome.model.script.engine.ScriptEngine;
import org.eclipse.smarthome.model.script.engine.ScriptExecutionException;
import org.eclipse.smarthome.model.script.engine.ScriptParsingException;

/**
 * This class provides the script engine as a console command
 *
 * @author Oliver Libutzki - Initial contribution
 *
 */
public class ScriptEngineConsoleCommandExtension extends AbstractConsoleCommandExtension {

    private ScriptEngine scriptEngine;

    public ScriptEngineConsoleCommandExtension() {
        super(">", "Execute scripts");
    }

    @Override
    public void execute(String[] args, Console console) {
        if (scriptEngine != null) {
            String scriptString = Arrays.stream(args).collect(Collectors.joining(" "));
            Script script;
            try {
                script = scriptEngine.newScriptFromString(scriptString);
                Object result = script.execute();

                if (result != null) {
                    console.println(result.toString());
                } else {
                    console.println("OK");
                }
            } catch (ScriptParsingException e) {
                console.println(e.getMessage());
            } catch (ScriptExecutionException e) {
                console.println(e.getMessage());
            }
        } else {
            console.println("Script engine is not available.");
        }
    }

    @Override
    public List<String> getUsages() {
        return Collections.singletonList(buildCommandUsage("<script to execute>", "Executes a script"));
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public void unsetScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = null;
    }
}
