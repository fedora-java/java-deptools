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

public class Main {
    public static void main(String[] args) {
        try {
            Cli app = new Cli(args);
            app.run();
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Unhandled exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}