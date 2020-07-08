package io.koschicken.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SafeProperties extends Properties {
    private static final long serialVersionUID = 5011694856722313621L;

    private static final String keyValueSeparators = "=: \t\r\n\f";

    private static final String strictKeyValueSeparators = "=:";

    private static final String whiteSpaceChars = " \t\r\n\f";

    private static final Charset file_code = StandardCharsets.UTF_8;
    /**
     * A table of hex digits
     */
    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F'};
    private final PropertiesContext context = new PropertiesContext();

    private static void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

    /**
     * Convert a nibble to a hex character
     *
     * @param nibble the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    public PropertiesContext getContext() {
        return context;
    }

    public synchronized void load(InputStream inStream) throws IOException {

        BufferedReader in;

        in = new BufferedReader(new InputStreamReader(inStream, file_code));
        while (true) {
            // Get next line
            String line = in.readLine();
            // intract property/comment string
            StringBuilder intactLine = new StringBuilder(line);
            if (line == null) {
                return;
            }

            if (line.length() > 0) {
                // Find start of key
                int len = line.length();
                int keyStart;
                for (keyStart = 0; keyStart < len; keyStart++)
                    if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                        break;

                // Blank lines are ignored
                if (keyStart == len)
                    continue;

                // Continue lines that end in slashes if they are not comments
                char firstChar = line.charAt(keyStart);

                if ((firstChar != '#') && (firstChar != '!')) {
                    while (continueLine(line)) {
                        String nextLine = in.readLine();
                        intactLine.append("\n").append(nextLine);
                        if (nextLine == null)
                            nextLine = "";
                        String loppedLine = line.substring(0, len - 1);
                        // Advance beyond whitespace on new line
                        int startIndex;
                        for (startIndex = 0; startIndex < nextLine.length(); startIndex++)
                            if (whiteSpaceChars.indexOf(nextLine.charAt(startIndex)) == -1)
                                break;
                        nextLine = nextLine.substring(startIndex);
                        line = loppedLine + nextLine;
                        len = line.length();
                    }

                    // Find separation between key and value
                    int separatorIndex;
                    for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
                        char currentChar = line.charAt(separatorIndex);
                        if (currentChar == '\\')
                            separatorIndex++;
                        else if (keyValueSeparators.indexOf(currentChar) != -1)
                            break;
                    }

                    // Skip over whitespace after key if any
                    int valueIndex;
                    for (valueIndex = separatorIndex; valueIndex < len; valueIndex++)
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;

                    // Skip over one non whitespace key value separators if any
                    if (valueIndex < len)
                        if (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1)
                            valueIndex++;

                    // Skip over white space after other separators if any
                    while (valueIndex < len) {
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;
                        valueIndex++;
                    }
                    String key = line.substring(keyStart, separatorIndex);
                    String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                    // Convert then store key and value
                    if (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']') {
                        String[] strings;
                        if (value.length() == 2) {
                            strings = new String[0];
                        } else {
                            strings = value.substring(1, value.length() - 1).split(",");
                        }
                        put(key, strings, intactLine.toString());
                    } else {
                        put(key, value, intactLine.toString());
                    }
                } else {
                    //memorize the comment string
                    context.addCommentLine(intactLine.toString());
                }
            } else {
                //memorize the string even the string is empty
                context.addCommentLine(intactLine.toString());
            }
        }
    }

    public synchronized void store(OutputStream out, String header) throws IOException {
        BufferedWriter awriter;
        awriter = new BufferedWriter(new OutputStreamWriter(out, file_code));
        if (header != null)
            writeln(awriter, "#" + header);
        List<Object> entries = context.getCommentOrEntries();
        for (Object obj : entries) {
            if (obj.toString() != null) {
                writeln(awriter, obj.toString());
            }
        }
        awriter.flush();
    }

    public synchronized Object put(Object key, Object value) {
        if (value.getClass().isArray()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            Object[] a = (Object[]) value;
            for (Object s : a) {
                stringBuilder.append(s).append(",");
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            stringBuilder.append("]");
            context.putOrUpdate(key.toString(), stringBuilder.toString());
        } else {
            context.putOrUpdate(key.toString(), value.toString());
        }
        return super.put(key, value);
    }

    public synchronized Object remove(Object key) {
        context.remove(key.toString());
        return super.remove(key);
    }

    private boolean continueLine(String line) {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }

    public synchronized Object put(Object key, Object value, String line) {
        context.putOrUpdate(key.toString(), value.toString(), line);
        return super.put(key, value);
    }

    /**
     * @param comment
     */
    public void addComment(String comment) {
        if (comment != null) {
            context.addCommentLine("#" + comment);
        }
    }

    /**
     * 添加空行
     */
    public void addVoidLine() {
        context.addCommentLine("");
    }

    static class PropertiesContext {
        private final List<Object> commentOrEntries = new ArrayList<>();

        public List<Object> getCommentOrEntries() {
            return commentOrEntries;
        }

        public void addCommentLine(String line) {
            commentOrEntries.add(line);
        }

        public void putOrUpdate(PropertyEntry pe) {
            remove(pe.getKey());
            commentOrEntries.add(pe);
        }

        public void putOrUpdate(String key, String value, String line) {
            PropertyEntry pe = new PropertyEntry(key, value, line);
            remove(key);
            commentOrEntries.add(pe);
        }

        public void putOrUpdate(String key, String value) {
            PropertyEntry pe = new PropertyEntry(key, value);
            int index = remove(key);
            commentOrEntries.add(index, pe);
        }

        public int remove(String key) {
            for (int index = 0; index < commentOrEntries.size(); index++) {
                Object obj = commentOrEntries.get(index);
                if (obj instanceof PropertyEntry) {
                    if (key.equals(((PropertyEntry) obj).getKey())) {
                        commentOrEntries.remove(obj);
                        return index;
                    }
                }
            }
            return commentOrEntries.size();
        }

        static class PropertyEntry {
            private String key;

            private String value;

            private String line;

            public PropertyEntry(String key, String value) {
                this.key = key;
                this.value = value;
            }

            /**
             * @param key
             * @param value
             * @param line
             */
            public PropertyEntry(String key, String value, String line) {
                this(key, value);
                this.line = line;
            }

            public String getLine() {
                return line;
            }

            public void setLine(String line) {
                this.line = line;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String toString() {
                if (line != null) {
                    return line;
                }
                if (key != null && value != null) {
                    return key + "=" + value;
                }
                return null;
            }
        }
    }
}
