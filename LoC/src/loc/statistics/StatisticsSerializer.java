package loc.statistics;

import loc.SerializeException;
import loc.filter.*;

import java.util.*;

public class StatisticsSerializer implements IStatisticsSerializer {
	private static char space              = ' ';
	private static String separator        = " - ";
	private static String lineBreak        = "\n";
	private static char underlineCharacter = '-';

	private Statistics statistics;
	private StringBuffer buffer;
	private List<String> filterStrings;
	private List<FileStatistics> fileStatistics;
	private int maxLineCountLength = 0;
	private int maxFileCountLength = 0;

	public static char getSpace() {
		return space;
	}

	public static void setSpace(char space) {
		StatisticsSerializer.space = space;
	}

	public static String getSeparator() {
		return separator;
	}

	public static void setSeparator(String separator) {
		StatisticsSerializer.separator = separator;
	}

	public static String getLineBreak() {
		return lineBreak;
	}

	public static void setLineBreak(String lineBreak) {
		StatisticsSerializer.lineBreak = lineBreak;
	}

	public static char getUnderlineCharacter() {
		return underlineCharacter;
	}

	public static void setUnderlineCharacter(char underlineCharacter) {
		StatisticsSerializer.underlineCharacter = underlineCharacter;
	}

	public StatisticsSerializer(Statistics statistics) {
		if (statistics == null) throw new NullPointerException("Statistics is null");
		this.statistics = statistics;
	}

	@Override
	public String serialize() throws SerializeException {
		buffer = new StringBuffer();
		filterStrings = new ArrayList<>();
		fileStatistics = new ArrayList<>();
		int maxFilterStringLength = 0;
		Set<Map.Entry<Filter, FileStatistics>> sortedStatistics = new TreeSet<>(new StatisticsComparator());
		sortedStatistics.addAll(statistics.getFilterFileStatisticsMap().entrySet());
		for (Map.Entry<Filter, FileStatistics> entry: sortedStatistics) {
			Filter filter = entry.getKey();
			String filterString = FilterFactory.getSerializer(filter).serialize(filter);
			filterStrings.add(filterString);
			fileStatistics.add(entry.getValue());
			maxFilterStringLength = Math.max(maxFilterStringLength, filterString.length());
		}
		appendSerialized(maxFilterStringLength);
		return buffer.toString();
	}

	private void calculateMaxLineAndFileCounts() {
		int maxLineCount = 0;
		int maxFileCount = 0;
		for (FileStatistics stats: fileStatistics) {
			maxLineCount = Math.max(maxLineCount, stats.lineCount);
			maxFileCount = Math.max(maxFileCount, stats.fileCount);
		}
		maxLineCountLength = String.valueOf(maxLineCount).length();
		maxFileCountLength = String.valueOf(maxFileCount).length();
	}

	private void appendSerialized(int maxFilterStringLength) {
		buffer.append("Total");
		buffer.append(separator);
		buffer.append(statistics.getTotalLineCount());
		buffer.append(" lines in ");
		buffer.append(statistics.getTotalFileCount());
		buffer.append(" files");
		buffer.append(lineBreak);
		append(underlineCharacter, maxFilterStringLength);
		buffer.append(lineBreak);

		calculateMaxLineAndFileCounts();

		for (int i = 0; i < filterStrings.size(); i++) {
			String filterString = filterStrings.get(i);
			buffer.append(filterString);
			append(space, maxFilterStringLength - filterString.length());
			buffer.append(separator);
			append(fileStatistics.get(i));
			buffer.append(lineBreak);
		}
	}

	private void append(char c, int amount) {
		for (int i = 0; i < amount; i++) {
			buffer.append(c);
		}
	}

	private void append(FileStatistics fileStatistics) {
		buffer.append(fileStatistics.lineCount);
		append(space, maxLineCountLength - String.valueOf(fileStatistics.lineCount).length());
		buffer.append(" lines in ");
		buffer.append(fileStatistics.fileCount);
		append(space, maxFileCountLength - String.valueOf(fileStatistics.fileCount).length());
		buffer.append(" files");
	}
}
