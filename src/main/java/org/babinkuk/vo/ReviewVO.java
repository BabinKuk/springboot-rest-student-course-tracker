package org.babinkuk.vo;

import org.babinkuk.vo.diff.DiffField;
import org.babinkuk.vo.diff.Diffable;

@Diffable(id = "id")
public class ReviewVO {

	private int id;
	
	@DiffField
	private String comment;
	
	public ReviewVO() {
		// TODO Auto-generated constructor stub
	}
	
	public ReviewVO(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
		
	@Override
	public String toString() {
		return "ReviewVO [id=" + id + ", comment=" + comment + "]";
	}	
}
