/*******************************************************************************
 * main.m                                                                      *
 * propertylistserialization Copyright (c) 2018; Electric Bolt Limited.        *
 ******************************************************************************/

// Run in 64-bit iOS Simulator - outputs to the console hex encoded binary plists.
// These hex encoded binary plists are used verbatim as 'xcodeTemplate's in
// the Android test classes BinaryPropertyListReaderTest and
// BinaryPropertyListWriterTest.

@import Foundation;

static void output(NSString* test, NSObject* object) {
    NSError* error = nil;
    NSData* data = [NSPropertyListSerialization dataWithPropertyList: object format: NSPropertyListBinaryFormat_v1_0 options: 0 error: &error];
    if (error != nil) {
        NSLog(@"Error %@", [error description]);
        exit(1);
    }

    NSString* hex = [[data description] stringByReplacingOccurrencesOfString: @" " withString: @""];
    hex = [hex substringWithRange: NSMakeRange(1, [hex length] - 2)];

    NSLog(@"%@ %@", test, hex);
}

static NSDate* dateFromString(NSString* dateStr) {
    NSDateFormatter* dateFormatter = [NSDateFormatter new];
    [dateFormatter setFormatterBehavior: NSDateFormatterBehavior10_4];
    dateFormatter.locale = [[NSLocale alloc] initWithLocaleIdentifier: @"en_US_POSIX"];
    dateFormatter.timeZone = [NSTimeZone timeZoneForSecondsFromGMT: 0];
    dateFormatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier: NSCalendarIdentifierGregorian];
    [dateFormatter setDateFormat: @"yyyy-MM-dd'T'HH:mm:ss'Z'"];
    NSDate* date = [dateFormatter dateFromString: dateStr];
    return date;
}

static NSData* makeData(int len) {
    NSMutableData* data = [[NSMutableData alloc] initWithCapacity: len];
    unsigned char v = 0;
    for (int i = 0; i < len; i++) {
        [data appendBytes: &v length: 1];
        v++;
    }
    return data;
}

static void outputReaderTestData(void) {
    output(@"YES", [NSNumber numberWithBool: YES]);
    output(@"NO", [NSNumber numberWithBool: NO]);

    // postive integers
    output(@"Integer 0", [NSNumber numberWithInteger: 0]);
    output(@"Integer 1", [NSNumber numberWithInteger: 1]);
    output(@"Integer 126", [NSNumber numberWithInteger: 126]);
    output(@"Integer 127", [NSNumber numberWithInteger: 127]);
    output(@"Integer 128", [NSNumber numberWithInteger: 128]);
    output(@"Integer 254", [NSNumber numberWithInteger: 254]);
    output(@"Integer 255", [NSNumber numberWithInteger: 255]);
    output(@"Integer 256", [NSNumber numberWithInteger: 256]);
    output(@"Integer 32766", [NSNumber numberWithInteger: 32766]);
    output(@"Integer 32767", [NSNumber numberWithInteger: 32767]);
    output(@"Integer 32768", [NSNumber numberWithInteger: 32768]);
    output(@"Integer 65534", [NSNumber numberWithInteger: 65534]);
    output(@"Integer 65535", [NSNumber numberWithInteger: 65535]);
    output(@"Integer 65536", [NSNumber numberWithInteger: 65536]);
    output(@"Integer 2147483646", [NSNumber numberWithInteger: 2147483646]);
    output(@"Integer 2147483647", [NSNumber numberWithInteger: 2147483647]);
    output(@"Integer 2147483648", [NSNumber numberWithInteger: 2147483648L]); // 64-bit
    output(@"Integer 9223372036854775806", [NSNumber numberWithInteger: 9223372036854775806L]);
    output(@"Integer 9223372036854775807", [NSNumber numberWithInteger: 9223372036854775807L]);

    // negative integers
    output(@"Integer -1", [NSNumber numberWithInteger: -1]);
    output(@"Integer -127", [NSNumber numberWithInteger: -127]);
    output(@"Integer -128", [NSNumber numberWithInteger: -128]);
    output(@"Integer -129", [NSNumber numberWithInteger: -129]);
    output(@"Integer -255", [NSNumber numberWithInteger: -255]);
    output(@"Integer -256", [NSNumber numberWithInteger: -256]);
    output(@"Integer -257", [NSNumber numberWithInteger: -257]);
    output(@"Integer -32767", [NSNumber numberWithInteger: -32767]);
    output(@"Integer -32768", [NSNumber numberWithInteger: -32768]);
    output(@"Integer -32769", [NSNumber numberWithInteger: -32769]);
    output(@"Integer -65534", [NSNumber numberWithInteger: -65534]);
    output(@"Integer -65535", [NSNumber numberWithInteger: -65535]);
    output(@"Integer -65536", [NSNumber numberWithInteger: -65536]);
    output(@"Integer -2147483647", [NSNumber numberWithInteger: -2147483647]);
    output(@"Integer -2147483648", [NSNumber numberWithInteger: -2147483648]);
    output(@"Integer -2147483649", [NSNumber numberWithInteger: -2147483649L]); // 64-bit
    output(@"Integer -9223372036854775807", [NSNumber numberWithInteger: -9223372036854775807L]);
    output(@"Integer -9223372036854775808", [NSNumber numberWithInteger: LONG_MIN]); // hardcoding -9223372036854775808 causes compiler to issue faulty warning

    // real (float)
    output(@"Float 0.0", [NSNumber numberWithFloat: 0.0]);
    output(@"Float 1.0", [NSNumber numberWithFloat: 1.0]);
    output(@"Float 2.5", [NSNumber numberWithFloat: 2.5]);
    output(@"Float 987654321.12345", [NSNumber numberWithFloat: 987654321.12345]);
    output(@"Float -1.0", [NSNumber numberWithFloat: -1.0]);
    output(@"Float -2.5", [NSNumber numberWithFloat: -2.5]);
    output(@"Float -987654321.12345", [NSNumber numberWithFloat: -987654321.12345]);

    // real (double)
    output(@"Double 0.0", [NSNumber numberWithDouble: 0.0]);
    output(@"Double 1.0", [NSNumber numberWithDouble: 1.0]);
    output(@"Double 2.5", [NSNumber numberWithDouble: 2.5]);
    output(@"Double 987654321.12345", [NSNumber numberWithDouble: 987654321.12345]);
    output(@"Double -1.0", [NSNumber numberWithDouble: -1.0]);
    output(@"Double -2.5", [NSNumber numberWithDouble: -2.5]);
    output(@"Double -987654321.12345", [NSNumber numberWithDouble: -987654321.12345]);

    // date
    output(@"Date 01/01/1970 12:00:00GMT", dateFromString(@"1970-01-01T12:00:00Z"));
    output(@"Date 25/06/1890 06:45:13GMT", dateFromString(@"1890-06-25T06:45:13Z"));
    output(@"Date 04/11/2019 14:22:59GMT", dateFromString(@"2019-11-04T14:22:59Z"));

    // data
    output(@"0 length", makeData(0));
    output(@"1 length", makeData(1));
    output(@"2 length", makeData(2));
    output(@"14 length", makeData(14));
    output(@"15 length", makeData(15));
    output(@"16 length", makeData(16));
    output(@"100 length", makeData(100));
    output(@"1000 length", makeData(1000));

    // ascii string
    output(@"empty string", @"");
    output(@"\" \" string", @" ");
    output(@"\"The dog jumped over the moon\" string", @"The dog jumped over the moon");

    // utf16 big endian string
    output(@"\"\u0100\" string", @"\u0100");
    output(@"\"\u0100\u0101\" string", @"\u0100\u0101");
    output(@"\"\u0100\u0101The cow jumped over the dog\u0102\u0103\" string", @"\u0100\u0101The cow jumped over the dog\u0102\u0103");

    NSMutableArray* array = [NSMutableArray new];
    output(@"emptyArray", array);

    [array addObject: [NSNumber numberWithInteger: 0]];
    [array addObject: [NSNumber numberWithFloat: 1.5]];
    [array addObject: [NSNumber numberWithDouble: 2.5]];
    [array addObject: [NSNumber numberWithBool: YES]];
    [array addObject: [NSNumber numberWithBool: NO]];
    [array addObject: makeData(5)];
    [array addObject: makeData(20)];
    [array addObject: dateFromString(@"1890-06-25T06:45:13Z")];
    [array addObject: @"The cow jumped over the dog"];
    [array addObject: @"\u0100\u0101The cow jumped over the dog\u0102\u0103"];
    output(@"filled array", array);

    NSMutableDictionary* dict = [NSMutableDictionary new];
    output(@"emptyDict", dict);

    dict = [NSMutableDictionary new];
    [dict setObject: [NSNumber numberWithInteger: 25] forKey: @"int"];
    output(@"25Dict", dict);

    dict = [NSMutableDictionary new];
    [dict setObject: [NSNumber numberWithInteger: 0] forKey: @"int"];
    [dict setObject: [NSNumber numberWithFloat: 1.5] forKey: @"float"];
    [dict setObject: [NSNumber numberWithDouble: 2.5] forKey: @"double"];
    [dict setObject: [NSNumber numberWithBool: YES] forKey: @"true"];
    [dict setObject: [NSNumber numberWithBool: NO] forKey: @"false"];
    [dict setObject: makeData(5) forKey: @"data5"];
    [dict setObject: makeData(20) forKey: @"data20"];
    [dict setObject: dateFromString(@"1890-06-25T06:45:13Z") forKey: @"date"];
    [dict setObject: @"The cow jumped over the dog" forKey: @"ascii"];
    [dict setObject: @"\u0100\u0101The cow jumped over the dog\u0102\u0103" forKey: @"utf16"];
    output(@"filledDict", dict);
}

int main(int argc, char * argv[]) {
    outputReaderTestData();
}
