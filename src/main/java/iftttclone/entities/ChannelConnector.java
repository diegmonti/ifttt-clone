package iftttclone.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iftttclone.json.ChannelSerializer;
import iftttclone.json.JsonViews;
import iftttclone.json.TimestampSerializer;

@Entity
@Table(name = "channel_connector")
public class ChannelConnector {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@Column(nullable = false)
	private String token;

	@JsonIgnore
	@Column(name = "refresh_token")
	private String refreshToken;

	@JsonSerialize(using = ChannelSerializer.class)
	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonSerialize(using = TimestampSerializer.class)
	@Column(name = "connection_time")
	private Long connectionTime;
	
	@JsonProperty(access = Access.READ_ONLY)
	@JsonView(JsonViews.Summary.class)
	private String account;
	
	@JsonView(JsonViews.Summary.class)
	@Transient
	private String permits;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(Long connectionTime) {
		this.connectionTime = connectionTime;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPermits() {
		return permits;
	}

	public void setPermits(String permits) {
		this.permits = permits;
	}

}
