package no.hvl.past.graph.trees;

import no.hvl.past.attributes.DataTypeDescription;
import no.hvl.past.names.Name;
import no.hvl.past.util.FileSystemUtils;

import java.nio.charset.Charset;
import java.util.List;

public class CsvLibrary {

    public static final class CsvFileConfiguration {
        private char delimitterChar;
        private char paranthesesChar;
        private Charset encoding;
        private FileSystemUtils.LineEnding lineEnding;
        private List<CsvColumn> columns;
    }


    public static final class CsvColumn {
        private final Name branch;
        private final String text;
        private final DataTypeDescription dataTypeDescription;

        public CsvColumn(Name branch, String text, DataTypeDescription dataTypeDescription) {
            this.branch = branch;
            this.text = text;
            this.dataTypeDescription = dataTypeDescription;
        }

    }

}
