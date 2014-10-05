
package net.cattaka.util.methodhttpexporter.util;

public class Converter {
    public static class ConverterException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ConverterException(String message) {
            super(message);
        }

        public ConverterException(Throwable cause) {
            super(cause);
        }
    }

    public static Boolean valueBooleanOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Boolean.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Boolean valuePrimBooleanOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Boolean.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Byte valueByteOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Byte.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Byte valuePrimByteOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Byte.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Character valueCharacterOf(String str) throws ConverterException {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            return str.charAt(0);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Character valuePrimCharacterOf(String str) throws ConverterException {
        if (str == null || str.length() == 0) {
            throw new ConverterException("null");
        }
        try {
            return str.charAt(0);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Short valueShortOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Short.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Short valuePrimShortOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Short.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Integer valueIntegerOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Integer valuePrimIntegerOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Long valueLongOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Long valuePrimLongOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Float valueFloatOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Float valuePrimFloatOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Double valueDoubleOf(String str) throws ConverterException {
        if (str == null) {
            return null;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }

    public static Double valuePrimDoubleOf(String str) throws ConverterException {
        if (str == null) {
            throw new ConverterException("null");
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            throw new ConverterException(e.getMessage());
        }
    }
}
