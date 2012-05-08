/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author gtri
 */
@Entity
public class StockStats 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@Column(name="correct")
	private Double correct;	
	
	@Column(name="incorrect")
	private Double incorrect;

	@Column(name="summary")
	private String summary;		

	@Column(name="classifier_name")
	private String classifier_name;

	@Column(name="confusion_matrix")
	private String confusion_matrix;

	public Double getCorrect() {
		return correct;
	}

	public void setCorrect(Double correct) {
		this.correct = correct;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getIncorrect() {
		return incorrect;
	}

	public void setIncorrect(Double incorrect) {
		this.incorrect = incorrect;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getClassifier_name() {
		return classifier_name;
	}

	public void setClassifier_name(String classifier_name) {
		this.classifier_name = classifier_name;
	}

	public String getConfusion_matrix() {
		return confusion_matrix;
	}

	public void setConfusion_matrix(String confusion_matrix) {
		this.confusion_matrix = confusion_matrix;
	}
	
	public void setConfusion_matrix(double[][] matrix)
	{
		StringBuilder sb = new StringBuilder("\ta\tb\tc\n");
		
		sb.append("a\t"+	matrix[0][0] + "\t" +	matrix[1][0] + "\t" +	matrix[2][0] + "\t\n");
		sb.append("b\t"+	matrix[0][1] + "\t" +	matrix[1][1] + "\t" +	matrix[2][1] + "\t\n");
		sb.append("c\t"+	matrix[0][2] + "\t" +	matrix[1][2] + "\t" +	matrix[2][2] + "\t\n");
				
		setConfusion_matrix(sb.toString());
	}
}
