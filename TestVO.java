package test.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="test")
public class DmailVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seqidx")
	private int idx;
	private String subject;
	private String qry;
			
	public DmailVO() {}
	

	public DmailVO(int idx, String subject, String qry) {
		super();
		this.idx = idx;
		this.subject = subject;
		this.qry = qry;
		
	}
	
	public int getidx() {
		return idx;
	}

	public void setidx(int seqidx) {
		this.idx = idx;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getQry() {
		return qry;
	}

	public void setQry(String qry) {
		this.qry = qry;
	}

	@Override
	public String toString() {
		return "Dmail [idx=" + idx + ", subject=" + subject + ", qry=" + qry + "]";
	}






	
	

}
