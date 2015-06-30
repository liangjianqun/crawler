package crawler.common;

import java.util.Arrays;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Pinyin {
	public static String getPinyin(String strCN)
			throws BadHanyuPinyinOutputFormatCombination {
		if (strCN == null) {
			return null;
		}
		StringBuffer spell = new StringBuffer();
		char[] charOfCN = strCN.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < charOfCN.length; i++) {
			if (charOfCN[i] > 128) {
				String[] spellArray = PinyinHelper.toHanyuPinyinStringArray(
						charOfCN[i], defaultFormat);
				if (spellArray != null) {
					spell.append(spellArray[0]);
				} else {
					spell.append(charOfCN[i]);
				}
			} else {
				spell.append(charOfCN[i]);
			}
		}
		return spell.toString();
	}

	public static String getFirstPinyin(String strCN)
			throws BadHanyuPinyinOutputFormatCombination {
		if (strCN == null) {
			return null;
		}
		StringBuffer firstSpell = new StringBuffer();
		char[] charOfCN = strCN.toCharArray();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < charOfCN.length; i++) {
			if (charOfCN[i] > 128) {
				String[] spellArray = PinyinHelper.toHanyuPinyinStringArray(
						charOfCN[i], format);
				if (null != spellArray) {
					firstSpell.append(spellArray[0].charAt(0));
				} else {
					firstSpell.append(charOfCN[i]);
				}
			} else {
				firstSpell.append(charOfCN[i]);
			}
		}
		return firstSpell.toString();
	}

	public static String[] getFirstAndPinyin(String strCN)
			throws BadHanyuPinyinOutputFormatCombination {
		if (strCN == null) {
			return null;
		}
		StringBuffer firstSpell = new StringBuffer();
		StringBuffer spell = new StringBuffer();
		char[] charOfCN = strCN.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < charOfCN.length; i++) {
			if (charOfCN[i] > 128) {
				String[] spellArray = PinyinHelper.toHanyuPinyinStringArray(
						charOfCN[i], defaultFormat);
				if (spellArray != null) {
					firstSpell.append(spellArray[0].charAt(0));
					spell.append(spellArray[0]);
				} else {
					firstSpell.append(charOfCN[i]);
					spell.append(charOfCN[i]);
				}
			} else {
				firstSpell.append(charOfCN[i]);
				spell.append(charOfCN[i]);
			}
		}
		return new String[] { firstSpell.toString(), spell.toString() };
	}

	public static void main(String[] args) {
		try {
			testPinyin();
		} catch (BadHanyuPinyinOutputFormatCombination e1) {
			e1.printStackTrace();
		}
	}

	static void testPinyin() throws BadHanyuPinyinOutputFormatCombination {
		String pinyin = null;
		String[] firstAndPinyin = null;
		String s = "< 中國-China-중국 >";

		pinyin = Pinyin.getFirstPinyin(s);
		logger.error("getFirstHanyuPinyin(" + s + ")......[" + pinyin
				+ "]");

		pinyin = Pinyin.getPinyin(s);
		logger.error("getHanyuPinyin(" + s + ")......[" + pinyin + "]");

		firstAndPinyin = Pinyin.getFirstAndPinyin(s);
		logger.error("getFirstAndHanyuPinyin(" + s + ")......"
				+ Arrays.asList(firstAndPinyin));
	}
}
