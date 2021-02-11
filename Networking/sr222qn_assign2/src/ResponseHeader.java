public class ResponseHeader {
	
	String statusCode;
	String date;
	String contentType;
	String location;
	
	public ResponseHeader(String statusCode, String date, String contentType) {
		super();
		this.statusCode = statusCode;
		this.date = date;
		this.contentType = contentType;
	}
	
	public ResponseHeader(String statusCode,String date , String contentType,  String location) {
		super();
		this.statusCode = statusCode;
		this.date = date;
		this.contentType = contentType;
		this.location = location;
	}

	public ResponseHeader() {
		
	}


	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}


	@Override
	public String toString() {
		if(location != null)
			return statusCode + date  + contentType + location;
		else
			return statusCode + date  + contentType;
	}

}
