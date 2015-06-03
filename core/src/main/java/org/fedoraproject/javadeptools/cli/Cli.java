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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.fedoraproject.javadeptools.ClassEntry;
import org.fedoraproject.javadeptools.Database;
import org.fedoraproject.javadeptools.Package;
import org.fedoraproject.javadeptools.impl.DatabaseFactory;

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
        options.addOption("V", "version", false,
                "print version information and exit");
        // options.addOption("list", false, "list all indexed packages");
    }

    Cli(String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        line = parser.parse(options, args);
        List<String> argList = Arrays.asList(line.getArgs());
        // TODO error handling
        this.command = argList.get(0);
        this.args = argList.subList(1, argList.size());
    }

    private void printClassEntry(ClassEntry c) {
        System.out.println(c.getFileArtifact().getPkg().getName() + " | "
                + c.getFileArtifact().getPath() + " | " + c.getClassName());
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

        String db_path = line.getOptionValue("database");

        Database db;
        if (db_path.startsWith("jdbc:")) {
            db = new DatabaseFactory(db_path).createDatabase();
        } else {
            db = new DatabaseFactory(new File(db_path)).createDatabase();
        }

        switch (command) {
        case "build":
            db.build(args.stream().map(File::new).collect(Collectors.toList()), true);
            return;
        case "list":
            Collection<Package> packages = db.getPackages();
            packages.forEach(p -> System.out.println(p.getName()));
            return;
        case "query":
            db.queryClasses('%' + args.get(0) + '%').getResults()
                    .forEach(this::printClassEntry);
        }

        assert false;
    }
}