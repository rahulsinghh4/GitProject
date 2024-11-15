package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Commit implements Serializable {

    // write to file using java.io.ObjectOutputStream
    // read file using java.io.ObjectInputStream
    // used in lab 11 already
    // commit contains files names and references to the blobs, which contain file contents, and a parent link

    private String _commitHash;
    private HashMap<String, String> _fileBlobs;
    private ArrayList<File> _files;
    private ArrayList<Blobs> _blobs;
    private String _time;
    private String _message;
    private String _parent;

    Commit(HashMap<String, String> map, String time, String message, String parent) throws ParseException {
        this._fileBlobs = map;
        this._commitHash = findCommitHash();
        this._parent = parent;
        // TODO create a commit hash
        formatTime(time);
        this._message = message;

    }

    public String findCommitHash() {
        byte [] commit = Utils.serialize(this);
        return Utils.sha1(commit);
    }
    public String getCommitHash() {
        return _commitHash;
    }
    public HashMap<String, String> getFileBlobs() {
        return _fileBlobs;
    }
    public ArrayList<Blobs> getBlobs() {
        return _blobs;
    }
    public ArrayList<File> getFiles() {
        return _files;
    }
    public String getTime() {
        return _time;
    }
    public String getMessage() {
        return _message;
    }
    public String getParent() {
        return _parent;
    }
    public void formatTime(String time) throws ParseException {
        _time = time;
        LocalDateTime commitTime = LocalDateTime.now();
        DateTimeFormatter fullPattern = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter timePattern = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDate date = LocalDate.now();
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateAsString = date.format(datePattern);
        Date date1 = new SimpleDateFormat("dd/M/yyyy").parse(dateAsString);

        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        int dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = new SimpleDateFormat("EE").format(dayOfWeekInt);

        int month = c.get(Calendar.MONTH);
        String monthAsString = new SimpleDateFormat("MMM").format(month);

        DateTimeFormatter dayOfMonthPattern = DateTimeFormatter.ofPattern("dd");
        String dayOfMonth = date.format(dayOfMonthPattern);

        DateTimeFormatter yearPattern = DateTimeFormatter.ofPattern("yyyy");
        String year = date.format(yearPattern);

        // Time Zone
        TimeZone tz = Calendar.getInstance().getTimeZone();
        ZoneId zone = ZoneId.of(TimeZone.getDefault().getID());
        ZonedDateTime zdt = commitTime.atZone(zone);
        ZoneOffset offset = zdt.getOffset();
        //int secondsOfHour = offset.getTotalSeconds() % (60 * 60);
        String timezone = String.format("%s%n", offset);
        timezone = timezone.replaceAll(":", "");

        // String timezone = String.format("%35s %10s%n", zone, offset);

        if (time == null) {
            _time = dayOfWeek + " " + monthAsString + " " + dayOfMonth + " " + commitTime.format(timePattern) + " " + year + " " + timezone;
            //System.out.println(_time);
        } else {

            DateFormat formatter= new SimpleDateFormat("dd HH:mm:ss yyyy Z");
            formatter.setTimeZone(TimeZone.getDefault());

            Date epo = new Date(0);
            c.setTime(epo);
            dayOfWeekInt = c.get(Calendar.DAY_OF_WEEK);
            dayOfWeek = new SimpleDateFormat("EE").format(dayOfWeekInt);

            month = c.get(Calendar.MONTH);
            monthAsString = new SimpleDateFormat("MMM").format(month);


            _time = dayOfWeek + " " + monthAsString + " " + formatter.format(epo);
            //System.out.println(formatter.format(date));
        }

    }

}
