/*-
 * Copyright (c) 2012-2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fedoraproject.javadeptools.cli;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fedoraproject.javadeptools.impl.JavaDeptoolsModule;

import com.google.inject.Injector;

class Cli {
    private final CommandLine line;
    private final List<String> args;
    private String command;

    private static Options options = new Options();
    static {
        options.addOption("d", "database", true,
                "specify database file location (required)");
        // options.addOption(
        // "p",
        // "provides",
        // false,
        // "print a set of classes provided by given Fedora package, RPM package or JAR file");
        // options.addOption("P", "what-provides", false,
        // "print a set of Fedora packages that provide the specified Java class");
        // options.addOption(
        // "q",
        // "requires",
        // false,
        // "print print a set of Fedora packages required by given Fedora package, RPM package or JAR file");
        // options.addOption("Q", "what-requires", false,
        // "print print a set of Fedora packages that require given Fedora package");
        // options.addOption("d", "diff", false,
        // "show differences between real and declared Fedora or RPM package requirements");
        // options.addOption("d", "diff", false,
        // "show differences between real and declared Fedora or RPM package requirements");
        // options.addOption("w", "why", false,
        // "explain why specified packages require given other package");
        // options.addOption("b", "build", false,
        // "build package database from specified directory");
        // options.addOption("h", "help", false,
        // "print help about usage and exit");
        options.addOption("debug", false,
                "Print additional debugging information");
        options.addOption("V", "version", false,
                "print version information and exit");
        // options.addOption("list", false, "list all indexed packages");
    }

    Cli(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        line = parser.parse(options, args);
        List<String> argList = Arrays.asList(line.getArgs());
        // TODO error handling
        command = argList.get(0);
        this.args = argList.subList(1, argList.size());
    }

    void run() throws Exception {
        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setArgName("pattern");
            formatter.printHelp("java-deptool", options);
            System.exit(0);
        }

        if (line.hasOption("version")) {
            System.out.println("java-deptools version 0");
            System.out.println("Copyright (c) 2012-2015 Red Hat, Inc.");
            System.out
                    .println("Written by Mikolaj Izdebski and Michael Simacek");
            System.exit(0);
        }

        String dbPath = line.getOptionValue("database");

        Map<String, String> dbProps = new HashMap<>();
        if (!dbPath.startsWith("jdbc:")) {
            dbPath = "jdbc:h2:file:" + new File(dbPath).getAbsolutePath();
        } else {
            String[] parts = dbPath.split(":");
            if (parts.length > 2 && parts[1].equals("postgresql")) {
                dbProps.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
            }
        }
        dbProps.put("javax.persistence.jdbc.url", dbPath);
        if (line.hasOption("debug")) {
            dbProps.put("hibernate.show_sql", "true");
            dbProps.put("hibernate.format_sql", "true");
        }
        System.out.println(dbProps);
        Injector injector = JavaDeptoolsModule.createInjector(dbProps);
        Commands commands = injector.getInstance(Commands.class);
        switch (command) {
        case "build":
            commands.build(args);
            return;
        case "list":
            commands.list();
            return;
        case "query":
            commands.query(args.get(0));
            return;
        }

        assert false;
    }
}