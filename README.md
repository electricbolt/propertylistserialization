# iOS compatible plist serialization and deserialization library for Android

## Features

### XML plist

* Character by character accurate output\*1 from method `PropertyListSerialization.dataFromPropertyList()` with iOS method `[NSPropertyListSerialization dataFromPropertyList:format:options:error:]`
* `dict` dictionaries are sorted by `key` (as per CFPropertyList.c)
* `key` (dictionary) and `string` values are escaped for `<` `>` and `&` characters to `\&lt;` `\&gt;` and `\&amp;` (as per CFPropertyList.c)

\*1 character by character accuracy excludes `<real>` numbers - the floating point representation output on Android will have on average 6 decimal places, compared to 12 decimal places on iOS).

### Binary plist

* Supports version `bplist00` constructs only (all data type conversions as per XML - if you can serialize/deserialize an object tree into XML, then you can serialize/deserialize the same object tree into Binary)
* Byte by byte accurate output\*2 from method `PropertyListSerialization.dataFromPropertyList()` with iOS method `[NSPropertyListSerialization dataFromPropertyList:format:options:error:]`

\*2 byte by byte accuracy excludes `<dict>` dictionaries with more than one `key/value` pair - unlike XML plists, they are not sorted by `key`, and therefore the ordering of the `key/value` pairs will differ.

## Distribution

* Minimum SDK 15 (4.0.3 Ice cream sandwich)
* Target SDK 26
* Full instrumented test suite
* Developed with Android Studio 3.1
* Friendly BSD-2 license

## Building instructions

To clean: `./gradlew clean`

To test: `./gradlew connectedAndroidTest` (on connected Android device).

To build: `./gradlew assembleRelease`. The AAR file will be output in `./propertylistserialization/build/outputs/aar/propertylistserialization-release.aar`

## XML Example

### Serialization

> Java

```java
List list = new ArrayList();
Map dict = new HashMap();
dict.put("Selected", true);
dict.put("IconName", "largeIcon.png")
dict.put("IconSize", 32);
list.add(dict);
try {
	byte[] result = PropertyListSerialization.dataWithPropertyList(list, Format.XML);
} catch(PropertyListSerializationException e) {
	...
}
```

### Results

> plist

```plist
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
	<array>
		<dict>
			<key>IconName</key>
			<string>largeIcon.png</string>
			<key>IconSize</key>
			<integer>32</integer>
			<key>Selected</key>
			<true/>
		</dict>
	</array>
</plist>
```

### Deserialization 

Input:

> Java

```java
InputStream is = ... // using the property list in 'Results' above.
List list;
try {
	list = (List) PropertyListSerialization.propertyListWithData(is, Format.XML);
} catch(PropertyListSerializationException e) {
	...
}
Map dict = (Map) list.get(0);
boolean selected = dict.get("Selected"); // true
String iconName = dict.get("IconName"); // "largeIcon.png"
int iconSize = dict.get("IconSize"); // 32
```

## Data type conversions

For both XML and Binary plist formats

plist element | Objective C data type | java type
--------------|-----------------------|----------
string | NSString | java.lang.String
integer | NSNumber (integerValue) | java.lang.Integer \*3
real | NSNumber (doubleValue) | java.lang.Double
real | NSNumber (floatValue) | java.lang.Float \*4
dict | NSDictionary | java.util.Map<String, Object>
array | NSArray | java.util.List
date | NSDate | java.util.Date
true | NSNumber (boolValue) YES | Boolean.valueOf(true)
false | NSNumber (boolValue) NO | Boolean.valueOf(false)
data | NSData | byte[]

\*3 32-bit values only.

\*4 Serialization only, deserialization will always output java.lang.Double.

## Class PropertyListSerialization

#### void dataWithPropertyList(Object,Format)

```java
public static @NonNull byte[] dataWithPropertyList(@NonNull Object obj, Format format) throws PropertyListWriteStreamException;
```

For the object graph provided, returns a property list as byte\[\] (encoded using utf8 for Format.XML)

Equivalent to iOS method `[NSPropertyList dataWithPropertyList:format:options:error]`

**params** *obj* - The object graph to write out as a property list. The object graph may only contain the following types: String, Integer, Float, Double, Map<String, Object>, List, Date, Boolean or byte[].

**params** *format* - Either Format.XML or Format.Binary

**returns** *byte\[\]* of the property list.

**throws** *PropertyListWriteStreamException* if the object graph is incompatible.

---

#### void writePropertyList(Object,OutputStream,Format)

```java
public static void writePropertyList(@NonNull Object obj, @NonNull OutputStream os, Format format) throws PropertyListWriteStreamException {
```

For the object graph provided, writes the property list (encoded using utf8 for Format.XML) to the output stream.

Equivalent to iOS method `[NSPropertyList writePropertyList:toStream:format:options:error]`

**params** *obj* - The object graph to write out as a property list. The object graph may only contain the following types: String, Integer, Float, Double, Map<String, Object>, List, Date, Boolean or byte\[\].

**params** *os* - The output stream to write the property list to.

**params** *format* - Either Format.XML or Format.Binary

**throws** *PropertyListWriteStreamException* if the object graph is incompatible.

---

#### Object propertyListWithData(byte\[\],Format)

```java
public static @NonNull Object propertyListWithData(@NonNull byte[] data, Format format) throws PropertyListReadStreamException;
```

Creates and returns a property list from the specified byte\[\].

Equivalent to iOS method `[NSPropertyList propertyListWithData:options:format:error]`

**params** *data* - For Format.XML - byte\[\] of property list (utf8 encoding). For Format.Binary - byte[] of binary plist.

**params** *format* - Either Format.XML or Format.Binary

**returns** Returns one of String, Integer, Double, Map<String, Object>, List, Date, Boolean or byte\[\].

**throws** *PropertyListReadStreamException* if the plist is corrupt, values could not be converted or the input stream is EOF.

---

#### Object propertyListWithData(InputStream,Format) 

```java
public static @NonNull Object propertyListWithData(@NonNull InputStream is, Format format) throws PropertyListReadStreamException;
```

Creates and returns a property list by reading from the specified input stream.

Equivalent to iOS method `[NSPropertyList propertyListWithStream:options:format:error]`

**params** *is* - For Format.XML - input stream of utf8 encoded string. For Format.Binary - input stream of binary plist.

**params** *format* - Either Format.XML or Format.Binary

**returns** Returns one of String, Integer, Double, Map<String, Object>, List, Date, Boolean or byte\[\].

**throws** *PropertyListReadStreamException* if the plist is corrupt, values could not be converted or the input stream is EOF.
