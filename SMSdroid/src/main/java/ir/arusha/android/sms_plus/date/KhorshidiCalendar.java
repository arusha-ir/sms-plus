package ir.arusha.android.sms_plus.date;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class KhorshidiCalendar {

    //  private
    private String[] threeLetterMonths = {"Far", "Ord", "Kho", "Tir", "Mor",
            "Sha", "Meh", "Abn", "Azr", "Dey", "Bah", "Esf"};
    private String[] months = {"Farvardin", "Ordibehesht", "Khordad", "Tir",
            "Mordad", "Shahrivar", "Mehr", "Aban", "Azar", "Dey", "Bahman",
            "Esfand"};
    private String[] farsiMonths = {
            "\u0641\u0631\u0648\u0631\u062f\u06cc\u0646",
            "\u0627\u0631\u062f\u06cc\u0628\u0647\u0634\u062a",
            "\u062e\u0631\u062f\u0627\u062f", "\u062a\u06cc\u0631",
            "\u0645\u0631\u062f\u0627\u062f",
            "\u0634\u0647\u0631\u06cc\u0648\u0631", "\u0645\u0647\u0631",
            "\u0622\u0628\u0627\u0646", "\u0622\u0630\u0631", "\u062f\u06cc",
            "\u0628\u0647\u0645\u0646", "\u0627\u0633\u0641\u0646\u062f"};
    private String[] threeLetterWeekDays = {"Yek", "DoS", "SeS", "Cha", "Pan",
            "Jom", "Sha",};
    private String[] weekDays = {"Yek Shanbe", "Do Shanbe", "Se Shanbe",
            "Chahar Shanbe", "Panj Shanbe", "Jom`e", "Shanbe"};
    private String[] farsiweekDays = {"\u06cc\u06a9\u0634\u0646\u0628\u0647",
            "\u062f\u0648\u0634\u0646\u0628\u0647",
            "\u0633\u0647\u200c\u0634\u0646\u0628\u0647",
            "\u0686\u0647\u0627\u0631\u0634\u0646\u0628\u0647",
            "\u067e\u0646\u062c\u200c\u0634\u0646\u0628\u0647",
            "\u062c\u0645\u0639\u0647", "\u0634\u0646\u0628\u0647",};
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    private int hour = 0;
    private int min = 0;
    private int sec = 0;
    private TimeZone timeZone = TimeZone.getDefault();

    public static void main(String[] args) {
        KhorshidiCalendar khCal = new KhorshidiCalendar();
        khCal.setEpoch(new Date().getTime());
        System.out.println(khCal.toString(true, false));
        try {
            khCal = convert("93/12/20");
            System.out.println(khCal.toString(true, false));
        } catch (Exception e) {
            System.out.println("wrong format");
        }
        try {
            khCal = convert("1450/12/20");
            System.out.println(khCal.toString(true, false));
        } catch (Exception e) {
            System.out.println("wrong format");
        }
        try {
            khCal = convert("1450/11/43");
            System.out.println(khCal.toString(true, false));
        } catch (Exception e) {
            System.out.println("wrong format");
        }
    }

    public static KhorshidiCalendar convert(String input) throws Exception {
        String[] splits = input.split("/");
        int year = Integer.parseInt(splits[0]);
        if (year < 100) year += 1300;
        int month = Integer.parseInt(splits[1]);
        int day = Integer.parseInt(splits[2]);
        if (year < 0 || year > 1500) throw new Exception();
        if (month < 1 || month > 12) throw new Exception();
        if (day < 1 || day > 31) throw new Exception();
        KhorshidiCalendar khCal = new KhorshidiCalendar();
        khCal.setYear(year);
        khCal.setMonth(month);
        khCal.setDay(day);
        return khCal;
    }

    public static String getCurrentDate(int hourDiff, int minDiff) {
        Calendar currentDate;
        KhorshidiCalendar currentKhDate;
        String timeDiff = "GMT" + ((hourDiff > 0) ? "+" : "") + hourDiff + ":"
                + ((minDiff < 10) ? "0" + minDiff : "" + minDiff);
        currentDate = Calendar.getInstance(TimeZone.getTimeZone(timeDiff));
        currentKhDate = new KhorshidiCalendar();
        currentKhDate.setCalendar(currentDate);
        return currentKhDate + "";
    }

    public static String getCurrentTime(int hourDiff, int minDiff) {
        long currentTime = System.currentTimeMillis() % (24 * 3600 * 1000);
        int hour = (int) (currentTime / 3600000);
        currentTime %= 3600000;
        int min = (int) (currentTime / 60000);
        min += minDiff;
        if (min >= 60) {
            hour++;
            min -= 60;
        }
        hour += hourDiff;
        if (hour >= 24) {
            hour -= 24;
        }
        return ((hour < 10) ? "0" + hour : "" + hour) + ":"
                + ((min < 10) ? "0" + min : "" + min);
    }

    public static KhorshidiCalendar get(int year, int month, int dayOfMonth) {
        KhorshidiCalendar calendar = new KhorshidiCalendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(dayOfMonth);
        return calendar;
    }

    @Override
    public String toString() {
        String s = year + "/" + ((month < 10) ? "0" : "") + month + "/"
                + ((day < 10) ? "0" : "") + day;
        return s.substring(2);
    }

    public String toString(boolean time) {
        return toString(true, time);
    }

    public String toString(boolean year, boolean time) {
        String s = (year ? (this.year + "/") : "") + ((month < 10) ? "0" : "")
                + month + "/" + ((day < 10) ? "0" : "") + day;
        if (time) {
            s += (" " + ((hour < 10) ? "0" + hour : hour) + ":"
                    + ((min < 10) ? "0" + min : min) + ":" + ((sec < 10) ? "0"
                    + sec : sec));

        }
        return s.substring(year ? 2 : 0);
    }

    /**
     * Get formatted string. You can enter any strings that contains these
     * characters: <ul> <li>$y: year (e.g. 91)
     * <p>
     * <li>$Y: complete year (e.g. 1391)
     * <p>
     * <li>$m: month (e.g. 1)
     * <p>
     * <li>$M: month string (e.g. "Farvardin", "Ordibehesht", ...)
     * <p>
     * <li>$n: month three-letter string (e.g. "Far", "Ord", "Kho", "Tir", ...)
     * <p>
     * <li>$N: Farsi month string (e.g. "\u0641\u0631\u0648\u06cc\u0631\u062f\u06cc\u0646", "\u0627\u0631\u062f\u06cc\u0628\u0647\u0634\u062a", ...)
     * <p>
     * <li>$d: day of month (e.g. 31)
     * <p>
     * <li>$D: day of week (e.g. 0, 1)
     * <p>
     * <li>$e: day of week string (e.g. "Shanbe", "Yekshanbe", ..)
     * <p>
     * <li>$E: two-letter week string (e.g. "Sha", "Yek", ..)
     * <p>
     * <li>$f: Farsi week day string (e.g. "\u0634\u0646\u0628\u0647")
     * <p>
     * <li>$h: 12-hour string without am-pm part (e.g. "11")
     * <p>
     * <li>$a: 12-hour am-pm string (e.g. "p.m")
     * <p>
     * <li>$b: 12-hour am-pm string (e.g. "\u0628\u0639\u062f \u0627\u0632 \u0638\u0647\u0631")
     * <p>
     * <li>$H: 24-hour string (e.g. "23")
     * <p>
     * <li>$i: minute string (e.g. "22")
     * <p>
     * <li>$s: second string (e.g. "21")
     * <p>
     * <li>$z: java standard time zone string (e.g. "Iran Standard Time")
     * <p>
     * </ul> You can add a number between 1 and 9 exact after each of this type to
     * make <b>space paddings</b>. <b>%</b> will be added for convert space padding
     * to <b>zero padding</b>.
     * <p>
     * Also you can add <b>~</b> character after string symbols to make them
     * <b>upper case</b>.<br> Also you can add <b>`</b> character after string
     * symbols to make them <b>lower case</b>.<br>
     * <p>
     * use <b>\</b> to write special characters: \\ , \$ , \%, \~ , \`<br>
     * <p>
     * if formatted text begins with ~, the output digits will be in Farsi.
     * <p>
     * Other characters will remain unchanged.<br><br>
     * <p>
     * Here is some examples:<br>
     * <p>
     * <blockquote> An example is <b>{@code "$y-$M~4-$d3"}</b> and output for "18
     * Farvardin" 1391 is {@code "91-FARV-
     * 18"} and for 4 Tir 1291 is {@code "91- TIR- 4"}<br><br>
     * <p>
     * Another example is <b>{@code "$y-$m-$d2%"}</b>. output for "4 Farvardin 1391"
     * is {@code "91-1-04"}<br>
     * <p>
     * Another example is <b><br> "~\u0631\u0648\u0632$d2% $N $y \u0633\u0627\u0639\u062a $h2%:$i2%:$s2% $b" <br></b>.
     * output for "12 Shahrivar 1391" is <br> \u0631\u0648\u0632 \u06f1\u06f2 \u0634\u0647\u0631\u06cc\u0648\u0631 \u06f9\u06f1 \u0633\u0627\u0639\u062a \u06f0\u06f3:\u06f2\u06f6:\u06f0\u06f5 \u0628\u0639\u062f \u0627\u0632
     * \u0638\u0647\u0631 <br>
     * <p>
     * </blockquote>
     *
     * @param format
     * @return text with the requested format, "Wrong text format!" in other cases.
     */
    public String toString(String format) {
        StringBuilder stream = new StringBuilder(format.length());
        /*
         * 0 means no padding. default is space padding. for zero padding it will be
		 * added with 20
		 */
        StringBuilder padding = new StringBuilder(format.length());
        /*
		 * u = upper, l = lower, n = no change
		 */
        StringBuilder uppercase = new StringBuilder(format.length());
		/*
		 * special character sequence
		 */
        StringBuilder special = new StringBuilder(format.length());

        char[] characters = format.toCharArray();
        boolean escape = false;
        boolean farsiDigits = false;
        /**
         * 0 means no special mode 1 means we have entered to special mode detected
         * after seeing a $ 2 means we have seen the special type (m,M,y,...)
         */
        int specialMode = 0;
        if ((characters.length > 0) && (characters[0] == '~'))
            farsiDigits = true;
        for (int i = 0; i < characters.length; i++) {
            char ch = characters[i];
            if (ch == '\\') {
                if (specialMode == 1) {
                    return "Wrong text format!";
                }
                specialMode = 0;
                escape = true;
                continue;
            }
            if (escape) {
                stream.append(ch);
                escape = false;
                continue;
            }

            if (ch == '$') {
                specialMode = 1;
                continue;
            }

            if (specialMode == 0) {
                stream.append(ch);
                continue;
            }

            if ((ch == 'y') || (ch == 'Y') || (ch == 'm') || (ch == 'M')
                    || (ch == 'n') || (ch == 'N') || (ch == 'd') || (ch == 'D')
                    || (ch == 'e') || (ch == 'E') || (ch == 'f') || (ch == 'h')
                    || (ch == 'a') || (ch == 'b') || (ch == 'H') || (ch == 'i')
                    || (ch == 's') || (ch == 'z')) {
                stream.append((char) 0);
                special.append(ch);
                uppercase.append('n');
                padding.append((char) 0);
                specialMode = 2;
                continue;
            }

            if (specialMode != 2) {
                return "Wrong text format!";
            }

            if ((ch >= '1') && (ch <= '9')) {
                char newPadding = (char) (ch - '0');
                if (padding.charAt(padding.length() - 1) >= 20) //it is number padded
                {
                    newPadding += 20;
                }
                padding.setCharAt(padding.length() - 1, newPadding);
                continue;
            }

            if (ch == '%') {
                char oldPadding = padding.charAt(padding.length() - 1);
                if (oldPadding < 20) //space padding
                {
                    padding.setCharAt(padding.length() - 1,
                            (char) (oldPadding + 20));
                }
                continue;
            }

            if (ch == '~') {
                uppercase.setCharAt(uppercase.length() - 1, 'u');
                continue;
            }

            if (ch == '`') {
                uppercase.setCharAt(uppercase.length() - 1, 'l');
                continue;
            }

            specialMode = 0;
            i--; //processing current character again
        }

        if (specialMode == 1) {
            return "Wrong text format!";
        }

        //generating output string
        StringBuilder output = new StringBuilder(20);
        for (int j = 0, specialIndex = 0; j < stream.length(); j++) {
            char ch = stream.charAt(j);
            if (ch != 0) {
                output.append(ch);
                continue;

            }
            String toWrite = null;

            //$y: year (e.g. 91) $Y: complete year (e.g. 1391) $m: month
            //(e.g. 1) $M: month string (e.g. "Farvardin", "Ordibehesht", ...) $n: month
            //three-letter string (e.g. "Far", "Ord", "Kho", "Tir", ...) $N: Farsi month
            //string (e.g. "\u0641\u0631\u0648\u06cc\u0631\u062f\u06cc\u0646", "\u0627\u0631\u062f\u06cc\u0628\u0647\u0634\u062a", ...) $d: day of month (e.g. 31) $D:
            //day of week (e.g. 0, 1) $e: day of week string (e.g. Shanbe, Yekshanbe,...)
            switch (special.charAt(specialIndex)) {
                case 'Y':
                    toWrite = String.valueOf(year);
                    break;
                case 'y':
                    toWrite = String.valueOf(year);
                    if (toWrite.length() >= 2) {
                        toWrite = toWrite.substring(toWrite.length() - 2);
                    }
                    break;
                case 'm':
                    toWrite = String.valueOf(month);
                    break;
                case 'M':
                    toWrite = months[month - 1];
                    break;
                case 'n':
                    toWrite = threeLetterMonths[month - 1];
                    break;
                case 'N':
                    toWrite = farsiMonths[month - 1];
                    break;
                case 'd':
                    toWrite = String.valueOf(day);
                    break;
                case 'D':
                    toWrite = String.valueOf(dayOfWeek);
                    break;
                case 'e':
                    toWrite = weekDays[dayOfWeek];
                    break;
                case 'E':
                    toWrite = threeLetterWeekDays[dayOfWeek];
                    break;
                case 'f':
                    toWrite = farsiweekDays[dayOfWeek];
                    break;
                case 'h':
                    if (hour < 12) {
                        toWrite = String.valueOf(hour);
                    } else if (hour == 12) {
                        toWrite = "12";
                    } else {
                        toWrite = String.valueOf(hour % 12);
                    }
                    break;
                case 'a':
                    if (hour < 12) {
                        toWrite = "am";
                    } else {
                        toWrite = "pm";
                    }
                    break;
                case 'b':
                    if (hour < 12) {
                        toWrite = "\u0635\u0628\u062d";
                    } else {
                        toWrite = "\u0628\u0639\u062f \u0627\u0632 \u0638\u0647\u0631";
                    }
                    break;
                case 'H':
                    toWrite = String.valueOf(hour);
                    break;
                case 'i':
                    toWrite = String.valueOf(min);
                    break;
                case 's':
                    toWrite = String.valueOf(sec);
                    break;
                case 'z':
                    toWrite = timeZone.getDisplayName();
                    break;
            }

            switch (uppercase.charAt(specialIndex)) {
                case 'u':
                    toWrite = toWrite.toUpperCase();
                    break;
                case 'l':
                    toWrite = toWrite.toLowerCase();
                    break;
            }

            int pad = padding.charAt(specialIndex);
            if (pad > 0) {
                char paddingChar = ' ';
                if (pad > 20) {
                    paddingChar = '0';
                    pad = (pad % 20);
                }
                toWrite = getPadded(toWrite, paddingChar, pad);
            }

            output.append(toWrite);
            specialIndex++;

        }
        if (farsiDigits) {
            for (int j = 0; j < output.length(); j++) {
                char ch = output.charAt(j);
                switch (ch) {
                    case '0':
                        output.setCharAt(j, '\u06f0');
                        break;
                    case '1':
                        output.setCharAt(j, '\u06f1');
                        break;
                    case '2':
                        output.setCharAt(j, '\u06f2');
                        break;
                    case '3':
                        output.setCharAt(j, '\u06f3');
                        break;
                    case '4':
                        output.setCharAt(j, '\u06f4');
                        break;
                    case '5':
                        output.setCharAt(j, '\u06f5');
                        break;
                    case '6':
                        output.setCharAt(j, '\u06f6');
                        break;
                    case '7':
                        output.setCharAt(j, '\u06f7');
                        break;
                    case '8':
                        output.setCharAt(j, '\u06f8');
                        break;
                    case '9':
                        output.setCharAt(j, '\u06f9');
                        break;
                }
            }
            return output.toString().substring(1);
        }
        return output.toString();
    }

    private String getPadded(String string, char paddingCharacter,
                             int paddingSize) {
        StringBuilder buffer = new StringBuilder(paddingSize);
        buffer.setLength(paddingSize);
        int i = 0;
        int stringSize = string.length();
        int effectiveSize = (stringSize < paddingSize)
                ? stringSize
                : paddingSize;
        for (; i < effectiveSize; i++) {
            buffer.setCharAt(paddingSize - i - 1, string.charAt(effectiveSize
                    - i - 1));
        }

        for (; i < paddingSize; i++) {
            buffer.setCharAt(paddingSize - i - 1, paddingCharacter);
        }

        return buffer.toString();
    }

    public String getMonthName() {
        return threeLetterMonths[month - 1];
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        int finalMonth = month;
        if (finalMonth < 1) {
            finalMonth = 12;
            year--;
        } else if (month > 12) {
            finalMonth = 1;
            year++;
        }

        this.month = finalMonth;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public void setEpoch(long epoch) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(epoch);
        setCalendar(cal);
    }

    public void setEpoch(long epoch, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(epoch);
        cal.setTimeZone(timeZone);
        setCalendar(cal);
    }

    public void setCalendar(Calendar calendar) {
        int miYear = calendar.get(Calendar.YEAR);
        int miMonth = calendar.get(Calendar.MONTH) + 1;
        int miDay = calendar.get(Calendar.DATE);

        int[] kMonth = {0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
        int[] mMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (((miYear % 4) == 0)
                && (!(((miYear % 100) == 0) && ((miYear % 400) != 0)))) {
            mMonth[2] = 29;
        }
        int addMonth = 0;
        for (int x = 1; x < miMonth; x++) {
            addMonth += mMonth[x];
        }
        int year2 = 0;
        for (int z = 1; z < miYear; z++)
            if (((z % 4) == 0) && (!(((z % 100) == 0) && ((z % 400) != 0))))
                year2 += 366;
            else
                year2 += 365;

        int total = year2 + addMonth + miDay - 226895;
        int h;
        for (h = 1; total >= 366; h++) {
            if ((((((h - ((h > 0) ? 474 : 473)) % 2820) + 512) * 682) % 2816) < 682) {
                total -= 366;
            } else {
                total -= 365;
            }
        }
        year = h;
        if (total == 0) {
            total = 365;
            year -= 1;
        }
        month = 0;
        day = 0;
        int allMonth = 0;
        int allMonth1 = 0;
        for (int l = 0; l < kMonth.length - 1; l++) {
            if ((((((year - ((year > 0) ? 474 : 473)) % 2820) + 512) * 682) % 2816) < 682) {
                kMonth[12] = 30;
            }
            allMonth += kMonth[l];
            allMonth1 += kMonth[l + 1];
            if (total > allMonth && total <= allMonth1) {
                month = l + 1;
                day = total - allMonth;
            }
        }

        timeZone = calendar.getTimeZone();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);
    }

    public Calendar getMiladi() {
        int miYear, miMonth, miDay;

        int[] kMonth = {0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};
        int[] mMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((((((year - ((year > 0) ? 474 : 473)) % 2820) + 512) * 682) % 2816) < 682) {
            kMonth[12] = 30;
        }

        int addMonth = 0;
        for (int x = 1; x < month; x++)
            addMonth += kMonth[x];
        int year2 = 0;
        for (int z = 1; z < year; z++)
            if (((((((z - ((z > 0) ? 474 : 473)) % 2820) + 474) + 38) * 682) % 2816) < 682)
                year2 += 366;
            else
                year2 += 365;
        int total = year2 + addMonth + day + 226895;
        int h;
        for (h = 1; total >= 366; h++) {
            if (((h % 4) == 0) && (!(((h % 100) == 0) && ((h % 400) != 0)))) {
                total -= 366;
            } else {
                total -= 365;
            }
        }
        miYear = h;
        int day1 = total;
        if (day1 == 0) {
            day1 = 365;
            miYear -= 1;
        }
        miMonth = 0;
        miDay = 0;
        int allMonth = 0;
        int allMonth1 = 0;
        int l;
        for (l = 0; l < mMonth.length - 1; l++) {
            if (((miYear % 4) == 0)
                    && (!(((miYear % 100) == 0) && ((miYear % 400) != 0)))) {
                mMonth[2] = 29;
            }
            allMonth += mMonth[l];
            allMonth1 += mMonth[l + 1];
            if (day1 > allMonth && day1 <= allMonth1) {
                miMonth = l + 1;
                miDay = day1 - allMonth;
            }
        }

        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.set(Calendar.YEAR, miYear);
        c.set(Calendar.MONTH, miMonth - 1);
        c.set(Calendar.DATE, miDay);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}