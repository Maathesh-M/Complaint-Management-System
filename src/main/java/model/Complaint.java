package model;
public class Complaint {
    private int    id;
    private int    userId;
    private String title;
    private String description;
    private String status;  // "Pending", "In Progress", "Resolved"
    private String date;    // Stored as String for easy display
    private String userName; // Joined from users table for display purposes
    public Complaint() {}
    public Complaint(int userId, String title, String description) {
        this.userId      = userId;
        this.title       = title;
        this.description = description;
        this.status      = "Pending"; // Default status
    }
    public Complaint(int id, int userId, String title, String description,
                     String status, String date) {
        this.id          = id;
        this.userId      = userId;
        this.title       = title;
        this.description = description;
        this.status      = status;
        this.date        = date;
    }
    public int    getId()          { return id; }
    public int    getUserId()      { return userId; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getStatus()      { return status; }
    public String getDate()        { return date; }
    public String getUserName()    { return userName; }
    public void setId(int id)                    { this.id          = id; }
    public void setUserId(int userId)            { this.userId      = userId; }
    public void setTitle(String title)           { this.title       = title; }
    public void setDescription(String desc)      { this.description = desc; }
    public void setStatus(String status)         { this.status      = status; }
    public void setDate(String date)             { this.date        = date; }
    public void setUserName(String userName)     { this.userName    = userName; }
    @Override
    public String toString() {
        return "Complaint{id=" + id + ", title='" + title + "', status='" +
               status + "', date='" + date + "'}";
    }
}