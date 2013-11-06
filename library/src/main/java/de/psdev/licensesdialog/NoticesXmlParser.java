/*
 * Copyright 2013 Philip Schiffer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.psdev.licensesdialog;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public final class NoticesXmlParser
{

	private NoticesXmlParser()
	{
	}

	public static Notices parse(final InputStream inputStream) throws Exception
	{
		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);
			parser.nextTag();
			return parse(parser);
		}
		finally
		{
			inputStream.close();
		}
	}

	private static Notices parse(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		Notices notices = new Notices();
		parser.require(XmlPullParser.START_TAG, null, "notices");
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("notice"))
			{
				notices.addNotice(readNotice(parser));
			}
			else
			{
				skip(parser);
			}
		}
		return notices;
	}

	private static Notice readNotice(XmlPullParser parser) throws IOException,
			XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "notice");
		String name = null;
		String url = null;
		String copyright = null;
		License license = null;
		while (parser.next() != XmlPullParser.END_TAG)
		{
			if (parser.getEventType() != XmlPullParser.START_TAG)
			{
				continue;
			}
			String element = parser.getName();
			if (element.equals("name"))
			{
				name = readName(parser);
			}
			else if (element.equals("url"))
			{
				url = readUrl(parser);
			}
			else if (element.equals("copyright"))
			{
				copyright = readCopyright(parser);
			}
			else if (element.equals("license"))
			{
				license = readLicense(parser);
			}
			else
			{
				skip(parser);
			}
		}
		return new Notice(name, url, copyright, license);
	}

	private static String readName(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		return readTag(parser, "name");
	}

	private static String readUrl(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		return readTag(parser, "url");
	}

	private static String readCopyright(XmlPullParser parser) throws IOException,
			XmlPullParserException
	{
		return readTag(parser, "copyright");
	}

	private static License readLicense(XmlPullParser parser) throws IOException,
			XmlPullParserException
	{
		String license = readTag(parser, "license");
		return LicenseResolver.read(license);
	}

	private static String readTag(XmlPullParser parser, String tag) throws IOException,
			XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, tag);
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, null, tag);
		return title;
	}

	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		String result = "";
		if (parser.next() == XmlPullParser.TEXT)
		{
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private static void skip(XmlPullParser parser)
	{
	}
}
