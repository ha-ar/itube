package co.vector.itube;


public class ItemDetails {
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        this.Id = id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
	public String getViewer() {
		return viewer;
	}
	public void setViewer(String viewer) {
		this.viewer = viewer;
	}
	public String getduration() {
		return duration;
	}
	public void setduration(String duration) {
		this.duration = duration;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
    public String getUploaddate() {
        return uploaddate;
    }
    public void setUploaddate(String uploaddate) {
        this.uploaddate = uploaddate;
    }
	
	private String name ;
	private String viewer;
	private String duration;
	private String image;
    private String author;
    private String uploaddate;
	private String Id;
}
