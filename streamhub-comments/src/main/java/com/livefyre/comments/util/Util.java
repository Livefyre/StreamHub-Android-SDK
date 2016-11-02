package com.livefyre.comments.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {



	public static CharSequence trimTrailingWhitespace(CharSequence source) {

		if (source == null)
			return "";

		int i = source.length();

		// loop back to the first non-whitespace character
		while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
		}

		return source.subSequence(0, i + 1);
	}

	@SuppressLint("SimpleDateFormat")
	public static String getFormatedDate(String rawDate,Boolean flag) {
		Calendar currenttime = Calendar.getInstance();
		Calendar commentTime = Calendar.getInstance();

		long nextDateInMillis = currenttime.getTimeInMillis();
		long commentTimeInMillis = Long.parseLong(rawDate) * 1000;
		commentTime.setTimeInMillis(commentTimeInMillis);

		long timeDifferenceMilliseconds = nextDateInMillis
				- commentTimeInMillis;
		long diffSeconds = timeDifferenceMilliseconds / 1000;
		long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
		long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
		long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
		// long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 *
		// 7);
		// long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 *
		// 1000 * 24 * 30.41666666));
		// long diffYears = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000
		// * 24 * 365));

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		if (flag) {
			if (diffSeconds < 1) {
				return "less than a second";
			} else if (diffMinutes < 1) {
				return diffSeconds + "s";
			} else if (diffHours < 1) {
				return diffMinutes + "m";
			} else if (diffDays < 1) {
				return diffHours + "h";
			} else if (diffDays < 7) {
				return diffDays + "d";
			} else {
				return sdf.format(commentTime.getTime());
			}
		} else {
			if (diffSeconds < 1) {
				return "Posted Just Now";
			} else if (diffMinutes < 1) {
				if (diffSeconds == 1)
					return diffSeconds+" second ago";
				else
					return diffSeconds+" seconds ago";
				
			} else if (diffHours < 1) {
				if (diffMinutes == 1)
					return diffMinutes+" minute ago";
				else
					return diffMinutes+" minutes ago";
			} else if (diffDays < 1) {
				if (diffHours == 1)
					return diffHours+" hour ago";
				else
					return diffHours+" hours ago";
			} else if (diffDays < 7) {
				if (diffDays == 1)
					return diffDays+" day ago";
				else
					return diffDays+" days ago";
			} else {
				
				
				
				return sdf.format(commentTime.getTime());
			}
		}
	}

	public static String getYoutubeVideoId(String youtubeUrl)
	{
		String video_id="";
		if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
		{

			String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			CharSequence input = youtubeUrl;
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);
			if (matcher.matches())
			{
				String groupIndex1 = matcher.group(7);
				if(groupIndex1!=null && groupIndex1.length()==11)
					video_id = groupIndex1;
			}
		}
		return video_id;
	}
}
