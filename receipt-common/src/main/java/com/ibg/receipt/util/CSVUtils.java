package com.ibg.receipt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * CSV工具类，基于<a href=
 * "https://github.com/FasterXML/jackson-dataformats-text/tree/master/csv">jackson-dataformats-text
 * csv</a> <br>
 * 可结合jackson注解使用，性能比apache commons
 * csv好一点，<a href="https://github.com/uniVocity/csv-parsers-comparison">评测参考</a>
 *
 * @author bobby
 *
 */
public final class CSVUtils {

	/**
	 * 对象列表转csv字符串
	 *
	 * @param list
	 *            - 对象列表
	 * @param clazz
	 *            - 对象类型
	 * @return 返回csv字符串
	 * @throws JsonProcessingException
	 */
	public static <T> String toCSV(List<T> list, Class<T> clazz) throws JsonProcessingException {
		return toCSV(list, clazz, false);
	}

	/**
	 * 对象列表转csv字符串
	 *
	 * @param list
	 *            - 对象列表
	 * @param clazz
	 *            - 对象类型
	 * @param withHeader
	 *            - 是否包含属性名称行
	 * @return 返回csv字符串
	 * @throws JsonProcessingException
	 */
	public static <T> String toCSV(List<T> list, Class<T> clazz, boolean withHeader) throws JsonProcessingException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(clazz);
		if (withHeader) {
			schema = schema.withHeader();
		}
		return mapper.writer(schema).writeValueAsString(list);
	}

    /**
     * 对象列表转csv字符串
     *
     * @param list
     *            - 对象列表
     * @param clazz
     *            - 对象类型
     * @param withHeader
     *            - 是否包含属性名称行
     * @param spiltChar
     *            - 分隔符
     * @return 返回csv字符串
     * @throws JsonProcessingException
     */
    public static <T> String toCSV(List<T> list, Class<T> clazz, boolean withHeader, char spiltChar) throws JsonProcessingException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withoutQuoteChar().withColumnSeparator(spiltChar);
        if (withHeader) {
            schema = schema.withHeader();
        }else{
            schema = schema.withoutHeader();
        }
        return mapper.writer(schema).writeValueAsString(list);
    }

	/**
	 * 对象列表转csv文件
	 *
	 * @param list
	 *            - 对象列表
	 * @param clazz
	 *            - 对象类型
	 * @param dest
	 *            - 目标文件
	 * @return 返回生成的文件
	 * @throws IOException
	 */
	public static <T> File toCSV(List<T> list, Class<T> clazz, File dest) throws IOException {
		return toCSV(list, clazz, false, dest);
	}

	/**
	 * 对象列表转csv文件
	 *
	 * @param list
	 *            - 对象列表
	 * @param clazz
	 *            - 对象类型
	 * @param withHeader
	 *            - 是否包含属性名称行
	 * @param dest
	 *            - 目标文件
	 * @return 返回生成的文件
	 * @throws IOException
	 */
	public static <T> File toCSV(List<T> list, Class<T> clazz, boolean withHeader, File dest) throws IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(clazz);
		if (withHeader) {
			schema = schema.withHeader();
		}
		mapper.writer(schema).writeValue(dest, list);

		return dest;
	}

	/**
	 * map列表转csv字符串，值都按字符串对待
	 *
	 * @param data
	 *            - 数据列表
	 * @param keyOrder
	 *            - 属性顺序
	 * @return - 返回生成的csv字符串
	 * @throws Exception
	 */
	public static String toCSV(List<Map<String, Object>> data, List<String> keyOrder) throws Exception {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder();
		for (String column : keyOrder) {
			schema.addColumn(column, ColumnType.STRING);
		}
		return mapper.writer(schema.build()).writeValueAsString(data);
	}

	/**
	 * map列表转csv字符串，值都按字符串对待
	 *
	 * @param data
	 *            - 数据列表
	 * @param columnTypeWithOrder
	 *            - map中的key及对应的值的类型，生成的csv也按照此顺序排列
	 * @return - 返回生成的csv字符串
	 * @throws Exception
	 */
	public static String toCSV(List<Map<String, Object>> data, LinkedHashMap<String, ColumnType> columnTypeWithOrder)
			throws Exception {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder();
		for (Entry<String, ColumnType> column : columnTypeWithOrder.entrySet()) {
			schema.addColumn(column.getKey(), column.getValue());
		}
		return mapper.writer(schema.build()).writeValueAsString(data);
	}

	/**
	 * map列表转csv文件，值都按字符串对待
	 *
	 * @param data
	 *            - 数据列表
	 * @param keyOrder
	 *            - 属性顺序
	 * @return - 返回生成的csv文件
	 * @throws Exception
	 */
	public static File toCSV(List<Map<String, Object>> data, List<String> keyOrder, File dest) throws Exception {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder();
		for (String column : keyOrder) {
			schema.addColumn(column, ColumnType.STRING);
		}
		mapper.writer(schema.build()).writeValue(dest, data);
		return dest;
	}

	/**
	 * map列表转csv文件
	 *
	 * @param data
	 *            - 数据列表
	 * @param columnTypeWithOrder
	 *            - map中的key及对应的值的类型，生成的csv也按照此顺序排列
	 * @param dest
	 *            - 目标文件
	 * @return - 返回生成的csv文件
	 * @throws Exception
	 */
	public static File toCSV(List<Map<String, Object>> data, LinkedHashMap<String, ColumnType> columnTypeWithOrder,
			File dest) throws Exception {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder();
		for (Entry<String, ColumnType> column : columnTypeWithOrder.entrySet()) {
			schema.addColumn(column.getKey(), column.getValue());
		}
		mapper.writer(schema.build()).writeValue(dest, data);
		return dest;
	}

	/**
     * 读取csv文件并转为对应的对象列表
     *
     * @param clazz
     *            - 对象类型
     * @param file
     *            - csv文件
     * @return 对象列表
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static <T> List<T> readCSV(Class<T> clazz, File file) throws JsonProcessingException, IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(true);
        MappingIterator<T> it = mapper.readerFor(clazz).with(schema).readValues(file);
        return it.readAll();
    }

    /**
     * 读取字节数组转为Object
     *
     * @param clazz
     *            - 对象类型
     * @param file
     *            - csv文件
     * @return 对象列表
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static <T> List<T> readCSV(Class<T> clazz, byte[] file, char separator,boolean skipFirstrow)
            throws JsonProcessingException, IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(skipFirstrow).withColumnSeparator(separator);
        MappingIterator<T> it = mapper.readerFor(clazz).with(schema).readValues(file);
        return it.readAll();
    }

	/**
	 * 读取字节数组转为Object
	 *
	 * @param clazz
	 *            - 对象类型
	 * @param file
	 *            - csv文件
	 * @return 对象列表
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static <T> List<T> readCSV(Class<T> clazz, byte[] file,boolean transferEnumWithToStringFlag, char separator,boolean skipFirstrow)
			throws JsonProcessingException, IOException {
		CsvMapper mapper = new CsvMapper();
		mapper = (CsvMapper)mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING,transferEnumWithToStringFlag);
		CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(skipFirstrow).withColumnSeparator(separator);
		MappingIterator<T> it = mapper.readerFor(clazz).with(schema).readValues(file);
		return it.readAll();
	}

    /**
     *
     * @param clazz
     * @param file
     * @param separator
     * @param skipFirstrow
     * @param <T>
     * @param skipLastRow
     * @return
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static <T> List<T> readCSV(Class<T> clazz, byte[] file, char separator,boolean skipFirstrow,boolean skipLastRow)
        throws JsonProcessingException, IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(skipFirstrow).withColumnSeparator(separator);
        MappingIterator<T> it = mapper.readerFor(clazz).with(schema).readValues(file);
        List list = it.readAll();
        if (CollectionUtils.isNotEmpty(list) && list.size() >= 1 && skipLastRow) {
            list.remove(list.size()-1);
        }
        return list;
    }

	/**
	 * 读取csv文件并转为map列表
	 *
	 * @param columnTypeWithOrder
	 *            - 每列对应的键及数据类型
	 * @param file
	 *            - csv文件
	 * @return map列表
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static List<Map<String, Object>> readCSV(LinkedHashMap<String, ColumnType> columnTypeWithOrder, File file)
			throws JsonProcessingException, IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder().setSkipFirstDataRow(true);
		for (Entry<String, ColumnType> column : columnTypeWithOrder.entrySet()) {
			schema.addColumn(column.getKey(), column.getValue());
		}

		MappingIterator<Map<String, Object>> it = mapper.readerFor(Map.class).with(schema.build()).readValues(file);
		return it.readAll();
	}

	/**
	 * 读取csv文件并转为对应的对象流
	 *
	 * @param clazz
	 *            - 对象类型
	 * @param file
	 *            - csv文件
	 * @return 对象流
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static <T> MappingIterator<T> readCSVStream(Class<T> clazz, File file) throws JsonProcessingException, IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(true);
		return mapper.readerFor(clazz).with(schema).readValues(file);
	}

    /**
     * 读取csv文件并转为对应的对象流
     *
     * @param clazz
     *            - 对象类型
     * @param file
     *            - csv文件
     * @param skipFirstRow
     *            - 是否跳过第一行
     * @param separator
     *            - 列分隔符
     * @return 对象流
     * @throws JsonProcessingException
     * @throws IOException
     */
    public static <T> MappingIterator<T> readCSVStream(Class<T> clazz, File file, boolean skipFirstRow, char separator) throws JsonProcessingException, IOException {
        CsvMapper mapper = new CsvMapper();
        if (clazz.isArray()) {
            mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        }
        CsvSchema schema = mapper.schemaFor(clazz).withSkipFirstDataRow(skipFirstRow).withColumnSeparator(separator);
        return mapper.readerFor(clazz).with(schema).readValues(file);
    }

	/**
	 * 读取csv文件并转为map流
	 *
	 * @param columnTypeWithOrder
	 *            - 每列对应的键及数据类型
	 * @param file
	 *            - csv文件
	 * @return map流
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public static MappingIterator<Map<String, Object>> readCSVStream(LinkedHashMap<String, ColumnType> columnTypeWithOrder, File file)
			throws JsonProcessingException, IOException {
		CsvMapper mapper = new CsvMapper();
		CsvSchema.Builder schema = new CsvSchema.Builder().setSkipFirstDataRow(true);
		for (Entry<String, ColumnType> column : columnTypeWithOrder.entrySet()) {
			schema.addColumn(column.getKey(), column.getValue());
		}

		return mapper.readerFor(Map.class).with(schema.build()).readValues(file);
	}

	private CSVUtils() {
	}
}
