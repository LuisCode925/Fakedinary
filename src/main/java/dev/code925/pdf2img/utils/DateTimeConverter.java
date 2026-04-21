
package dev.code925.pdf2img.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class DateTimeConverter {
    
    public static LocalDateTime toLocalDateTime(Calendar calendar){
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
    }

}
