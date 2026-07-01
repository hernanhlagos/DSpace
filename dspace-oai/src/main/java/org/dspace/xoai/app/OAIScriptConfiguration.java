/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.xoai.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.dspace.scripts.configuration.ScriptConfiguration;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;

/**
 * Script configuration for the OAI-PMH manager.
 */
public class OAIScriptConfiguration<T extends XOAI> extends ScriptConfiguration<T> {

    private static final String COMMAND_IMPORT = "import";
    private static final String COMMAND_CLEAN_CACHE = "clean-cache";
    private static final String COMMAND_COMPILE_ITEMS = "compile-items";
    private static final String COMMAND_ERASE_COMPILED_ITEMS = "erase-compiled-items";

    private static final ConfigurationService configurationService = DSpaceServicesFactory.getInstance()
            .getConfigurationService();

    private Class<T> dspaceRunnableClass;

    @Override
    public Class<T> getDspaceRunnableClass() {
        return dspaceRunnableClass;
    }

    @Override
    public void setDspaceRunnableClass(Class<T> dspaceRunnableClass) {
        this.dspaceRunnableClass = dspaceRunnableClass;
    }

    @Override
    public Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg()
                .desc(getActionDescription())
                .build());

        if (isSolrStorage()) {
            options.addOption("c", "clear", false, "Clear index before indexing (import only)");
        }

        options.addOption("v", "verbose", false, "Verbose output");

        return options;
    }

    private String getActionDescription() {
        if (isSolrStorage()) {
            return "OAI action to execute: " + COMMAND_IMPORT + ", " + COMMAND_CLEAN_CACHE;
        }

        return "OAI action to execute: " + COMMAND_CLEAN_CACHE + ", "
                + COMMAND_COMPILE_ITEMS + ", " + COMMAND_ERASE_COMPILED_ITEMS;
    }

    private boolean isSolrStorage() {
        return !"database".equals(configurationService.getProperty("oai.storage", "solr"));
    }
    
    @Override
    public List<String> getAllowedValues(Option option) {
        if ("action".equals(option.getLongOpt())) {
            return getAllowedActions();
        }

        return Collections.emptyList();
    }

    private List<String> getAllowedActions() {
        if (isSolrStorage()) {
            return Arrays.asList(COMMAND_IMPORT, COMMAND_CLEAN_CACHE);
        }

        return Arrays.asList(
                COMMAND_CLEAN_CACHE,
                COMMAND_COMPILE_ITEMS,
                COMMAND_ERASE_COMPILED_ITEMS
        );
    }
    
}