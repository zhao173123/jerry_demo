package socket.okhttp.dto;

/**
 * 接收百度地图根据地址查询经纬度DTO
 * 
 * @author jerry
 *
 */
public final class BaiduMapGeoCodeDTO {

	private Integer status;
	private Result result;

	public static class Result {
		private Location location;
		private Integer precise;
		private Integer confidence;
		private String level;

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public Integer getPrecise() {
			return precise;
		}

		public void setPrecise(Integer precise) {
			this.precise = precise;
		}

		public Integer getConfidence() {
			return confidence;
		}

		public void setConfidence(Integer confidence) {
			this.confidence = confidence;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

	}

	public static class Location {

		private float lat;// 纬度值
		private float lng;// 经度值

		public float getLat() {
			return lat;
		}

		public void setLat(float lat) {
			this.lat = lat;
		}

		public float getLng() {
			return lng;
		}

		public void setLng(float lng) {
			this.lng = lng;
		}

		@Override
		public String toString() {
			return "经度值:" + lng + ",纬度值" + lat;
		}

	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
