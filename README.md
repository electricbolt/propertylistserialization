# iOS compatible plist [de]serialization library for Android

## Features

* Compatible with XML style property lists
* Character by character accurate output\*1 from method `PropertyListSerialization.dataFromPropertyList()` with iOS method `[NSPropertyListSerialization dataFromPropertyList:format:options:error:]`
* `dict` dictionaries are sorted by `key` (as per CFPropertyList.c)
* `key` (dictionary) and `string` values are escaped for `<` `>` and `&` characters to `\&lt;` `\&gt;` and `\&amp;` (as per CFPropertyList.c)

\*1 character by character accuracy excludes `<real>` numbers - the floating point representation output on Android will have on average 6 decimal places, compared to 12 decimal places on iOS).

## Distribution

* Minimum SDK 15 (4.0.3 Ice cream sandwich)
* Target SDK 26
* Full instrumented test suite
* Developed with Android Studio 3.0.1
* Friendly BSD-2 license

## Building instructions

To clean: `./gradlew clean`

To test: `./gradlew connectedAndroidTest` (on connected Android device).

To build: `./gradlew assembleRelease`. The AAR file will be output in `./propertylistserialization/build/outputs/aar/propertylistserialization-release.aar`

## Example

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
	byte[] result = PropertyListSerialization.dataWithPropertyList(list);
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
	list = (List) PropertyListSerialization.propertyListWithData(is);
} catch(PropertyListSerializationException e) {
	...
}
Map dict = (Map) list.get(0);
boolean selected = dict.get("Selected"); // true
String iconName = dict.get("IconName"); // "largeIcon.png"
int iconSize = dict.get("IconSize"); // 32
```

## Data type conversions

plist element | Objective C data type | java type
--------------|-----------------------|----------
string | NSString | java.lang.String
integer | NSNumber (integerValue) | java.lang.Integer
real | NSNumber (doubleValue) | java.lang.Double
real | NSNumber (floatValue) | java.lang.Float*2
dict | NSDictionary | java.util.Map<String, Object>
array | NSArray | java.util.List
date | NSDate | java.util.Date
true | NSNumber (boolValue) YES | Boolean.valueOf(true)
false | NSNumber (boolValue) NO | Boolean.valueOf(false)
data | NSData | byte[]

*2 Serialization only, deserialization will use java.lang.Double.

## Class PropertyListSerialization

#### dataWithPropertyList(Object)

```java
public static @NonNull byte[] dataWithPropertyList(@NonNull, Object obj) throws PropertyListWriteStreamException;
```

For the object graph provided, returns a property list as byte[] encoded using utf8.

Equivalent to iOS method `[NSPropertyList dataWithPropertyList:format:options:error]`

**params** *obj* - The object graph to write out as a property list. The object graph may only contain the following types: String, Integer, Float, Double, Map<String, Object>, List, Date, Boolean or byte[].

**returns** *byte[]* of the property list.

**throws** *PropertyListWriteStreamException* if the object graph is incompatible.

---

#### writePropertyList(Object,OutputStream)

```java
public static void writePropertyList(@NonNull Object obj, @NonNull OutputStream os) throws PropertyListWriteStreamException {
```

For the object graph provided, writes the property list encoded using utf8 to the output stream.

Equivalent to iOS method `[NSPropertyList writePropertyList:toStream:format:options:error]`

**params** *obj* - The object graph to write out as a property list. The object graph may only contain the following types: String, Integer, Float, Double, Map<String, Object>, List, Date, Boolean or byte[].

**params** *os* - The output stream to write the property list to.

**throws** *PropertyListWriteStreamException* if the object graph is incompatible.

---

#### propertyListWithData(byte[])

```java
public static @NonNull Object propertyListWithData(@NonNull byte[] data) throws PropertyListReadStreamException;
```

Creates and returns a property list from the specified byte[].

Equivalent to iOS method `[NSPropertyList propertyListWithData:options:format:error]`

**params** *data* - byte[] of property list (utf8 encoding).

**returns** Returns one of String, Integer, Double, Map<String, Object>, List, Date, Boolean or byte[].

**throws** *PropertyListReadStreamException* if the plist is corrupt, values could not be converted or the input stream is EOF.

---

#### propertyListWithData(InputStream) 

```java
public static @NonNull Object propertyListWithData(@NonNull InputStream is) throws PropertyListReadStreamException;
```

Creates and returns a property list by reading from the specified input stream.

Equivalent to iOS method `[NSPropertyList propertyListWithStream:options:format:error]`

**params** *data* - byte[] of property list (utf8 encoding).

**returns** Returns one of String, Integer, Double, Map<String, Object>, List, Date, Boolean or byte[].

**throws** *PropertyListReadStreamException* if the plist is corrupt, values could not be converted or the input stream is EOF.
