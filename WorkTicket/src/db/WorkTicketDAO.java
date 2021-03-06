/**
 * 
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author TeamJoey
 *
 */
public class WorkTicketDAO {

	/**
	 * 
	 */
	protected PreparedStatement selectAnnotationStatement;
	protected PreparedStatement selectAllAnnotationStatement;
	protected PreparedStatement updateAnnotationStatement;
	protected PreparedStatement insertAnnotationStatement;
	protected PreparedStatement selectTicketAnnotationsStatement;
	protected PreparedStatement selectTicketStatement;
	protected PreparedStatement selectAllTicketStatement;
	protected PreparedStatement selectAllTicketAssignedToStatement;
	protected PreparedStatement updateTicketStatement;
	protected PreparedStatement insertTicketStatement;
	protected PreparedStatement selectAllUserStatement;
	protected PreparedStatement selectUserStatement;
	protected PreparedStatement updateUserStatement;
	protected PreparedStatement insertUserStatement;
	
	
	/**
	 * 
	 */
	public WorkTicketDAO() {
		
		try {

			// AMAZON RDS configuration
			//String jdbcUrl = "jdbc:mysql://mist7510workticket.cxja2uv5sze4.us-east-1.rds.amazonaws.com:3306/workticket";
			//String username = "awsuser";
			//String password = "wax&sh1ne";
			
			// LOCALHOST configuration
			String jdbcUrl = "jdbc:mysql://localhost:3306/workticket";
			String username = "awsuser";
			String password = "abc123";

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
			
			selectAnnotationStatement = conn.prepareStatement("SELECT annotationId, ticketId, authorUsername, text, datePosted FROM annotation WHERE annotationId = ?");
			selectAllAnnotationStatement = conn.prepareStatement("SELECT annotationId, ticketId, authorUsername, text, datePosted FROM annotation ORDER BY datePosted DESC");
			updateAnnotationStatement = conn.prepareStatement("UPDATE annotation SET ticketId = ?, authorUsername = ?, text = ?, datePosted = ? WHERE annotationId = ?");
			insertAnnotationStatement = conn.prepareStatement("INSERT INTO annotation (ticketId, authorUsername, text, datePosted) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			selectTicketAnnotationsStatement = conn.prepareStatement("SELECT annotationId, ticketId, authorUsername, text, datePosted FROM annotation WHERE ticketId = ? ORDER BY datePosted DESC");
			
			selectTicketStatement = conn.prepareStatement("SELECT ticketId, datePosted, title, description, assignedTo FROM ticket WHERE ticketId = ?");
			selectAllTicketStatement = conn.prepareStatement("SELECT ticketId, datePosted, title, description, assignedTo FROM ticket ORDER BY datePosted ASC");
			selectAllTicketAssignedToStatement = conn.prepareStatement("SELECT ticketId, datePosted, title, description, assignedTo FROM ticket WHERE assignedTo = ? ORDER BY datePosted ASC");
			updateTicketStatement = conn.prepareStatement("UPDATE ticket SET datePosted = ?, title = ?, description = ?, assignedTo = ? WHERE ticketId = ?");
			insertTicketStatement = conn.prepareStatement("INSERT INTO ticket (datePosted, title, description, assignedTo) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			selectUserStatement = conn.prepareStatement("SELECT username, passHash, email, name, role FROM user WHERE username = ?");
			selectAllUserStatement = conn.prepareStatement("SELECT username, passHash, email, name, role FROM user ORDER BY username");
			updateUserStatement = conn.prepareStatement("UPDATE user SET passHash = ?, email = ?, name = ?, role = ? WHERE username = ?");
			insertUserStatement = conn.prepareStatement("INSERT INTO user (username, passHash, email, name, role) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			// SQL to query the database.
		} catch (Exception e) {
			//print out error for debugging.
			System.out.println(e.getClass().getName() + " opening connection: " + e.getMessage());

		}
	}
	
	/**
	 * 
	 * @param annotationId
	 * @return
	 */
	public AnnotationDTO loadAnnotation(int annotationId) {
		try{		
			selectAnnotationStatement.setInt(1, annotationId);
			ResultSet rs = selectAnnotationStatement.executeQuery();
			while (rs.next()) {
				return new AnnotationDTO(
						rs.getInt(1), // annotationId
						rs.getInt(2), // ticketId
						rs.getString(3), // authorName
						rs.getString(4), // text
						rs.getDate(5)); // datePosted 
			}
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving annotation: " + e.getMessage());
		}
		return new AnnotationDTO();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<AnnotationDTO> listAnnotations(){
		try{		
			ResultSet rs = selectAllAnnotationStatement.executeQuery();
			ArrayList<AnnotationDTO> annotations = new ArrayList<AnnotationDTO>();
			while (rs.next()) {
				annotations.add(new AnnotationDTO(
									rs.getInt(1), // annotationId
									rs.getInt(2), // ticketId
									rs.getString(3), // authorUsername
									rs.getString(4), // text
									rs.getDate(5)	// datePosted
									));  
			}
			return annotations;
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving annotation: " + e.getMessage());
		}
		return new ArrayList<AnnotationDTO>();
	}
	
	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public ArrayList<AnnotationDTO> listTicketAnnotations(int ticketId){
		try{		
			selectTicketAnnotationsStatement.setInt(1, ticketId);
			ResultSet rs = selectTicketAnnotationsStatement.executeQuery();
			ArrayList<AnnotationDTO> annotations = new ArrayList<AnnotationDTO>();
			while (rs.next()) {
				annotations.add(new AnnotationDTO(
									rs.getInt(1), // annotationId
									rs.getInt(2), // ticketId
									rs.getString(3), // authorUsername
									rs.getString(4), // text
									rs.getDate(5)	// datePosted
									));  
			}
			return annotations;
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving annotation: " + e.getMessage());
		}
		return new ArrayList<AnnotationDTO>();
	}
	
	/**
	 * 
	 * @param annotation
	 * @return
	 */
	public int saveAnnotation(AnnotationDTO annotation) {
		try{
			if (annotation.getAnnotationId() > 0) {
				
				updateAnnotationStatement.setInt(1, annotation.getAnnotationId());
				updateAnnotationStatement.setInt(2, annotation.getTicketId());
				updateAnnotationStatement.setString(3, annotation.getAuthorUsername());
				updateAnnotationStatement.setString(4, annotation.getText());
				updateAnnotationStatement.setDate(5, new java.sql.Date(annotation.getDatePosted().getTimeInMillis()));
				updateAnnotationStatement.executeUpdate();
				return annotation.getAnnotationId();
			}
			else {
				insertAnnotationStatement.setInt(1, annotation.getTicketId());
				insertAnnotationStatement.setString(2, annotation.getAuthorUsername());
				insertAnnotationStatement.setString(3, annotation.getText());
				insertAnnotationStatement.setDate(4, new java.sql.Date(annotation.getDatePosted().getTimeInMillis()));
				insertAnnotationStatement.executeUpdate();
				ResultSet keys = insertAnnotationStatement.getGeneratedKeys();
				keys.next();
				return keys.getInt(1);
			}
		}
		catch (SQLException e) {
			System.out.println("Exception saving annotation: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	public TicketDTO loadTicket(int ticketId) {
		try{		
			selectTicketStatement.setInt(1, ticketId);
			ResultSet rs = selectTicketStatement.executeQuery();
			while (rs.next()) {
				return new TicketDTO(
						rs.getInt(1), // ticketId
						rs.getDate(2), // datePosted
						rs.getString(3), // title
						rs.getString(4), // description
						rs.getString(5)); // assignedTo 
			}
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving ticket: " + e.getMessage());
		}
		return new TicketDTO();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<TicketDTO> listTickets() {
		try{		
			ResultSet rs = selectAllTicketStatement.executeQuery();
			ArrayList<TicketDTO> tickets = new ArrayList<TicketDTO>();
			while (rs.next()) {
				tickets.add(new TicketDTO(
							rs.getInt(1), // ticketId
							rs.getDate(2), // datePosted
							rs.getString(3), // title
							rs.getString(4), // description
							rs.getString(5) // assignedTo 
							));
			}
			return tickets;
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving ticket: " + e.getMessage());
		}
		return new ArrayList<TicketDTO>();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<TicketDTO> listTickets(String assignedTo) {
		try{
			selectAllTicketAssignedToStatement.setString(1, assignedTo);
			ResultSet rs = selectAllTicketAssignedToStatement.executeQuery();
			ArrayList<TicketDTO> tickets = new ArrayList<TicketDTO>();
			while (rs.next()) {
				tickets.add(new TicketDTO(
							rs.getInt(1), // ticketId
							rs.getDate(2), // datePosted
							rs.getString(3), // title
							rs.getString(4), // description
							rs.getString(5) // assignedTo 
							));
			}
			return tickets;
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving ticket: " + e.getMessage());
		}
		return new ArrayList<TicketDTO>();
	}
	
	/**
	 * 
	 * @param ticket
	 * @return
	 */
	public int saveTicket(TicketDTO ticket) {
		try{
			if (ticket.getTicketId() > 0) {
				updateTicketStatement.setDate(1, new java.sql.Date(ticket.getDatePosted().getTimeInMillis()));
				updateTicketStatement.setString(2, ticket.getTitle());
				updateTicketStatement.setString(3, ticket.getDescription());
				updateTicketStatement.setString(4, ticket.getAssignedTo());
				updateTicketStatement.setInt(5, ticket.getTicketId());
				updateTicketStatement.executeUpdate();
				return ticket.getTicketId();
			}
			else {
				insertTicketStatement.setDate(1, new java.sql.Date(ticket.getDatePosted().getTimeInMillis()));
				insertTicketStatement.setString(2, ticket.getTitle());
				insertTicketStatement.setString(3, ticket.getDescription());
				insertTicketStatement.setString(4, ticket.getAssignedTo());
				insertTicketStatement.executeUpdate();
				ResultSet keys = insertTicketStatement.getGeneratedKeys();
				keys.next();
				return keys.getInt(1);
			}
		}
		catch (SQLException e) {
			System.out.println("Exception saving ticket: " + e.getMessage());
		}
		return 0;
	}

	public ArrayList<UserDTO> listUsers(){
		try{		
			ResultSet rs = selectAllUserStatement.executeQuery();
			ArrayList<UserDTO> list = new ArrayList<UserDTO>();
			while (rs.next()) {
				list.add(new UserDTO(
						rs.getString(1), 	// userName
						rs.getString(2), 	// passhash
						rs.getString(3), 	// email
						rs.getString(4),	// name
						rs.getString(5)));	//role 
			}
			return list;
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving list of users: " + e.getMessage());
		}
		return new ArrayList<UserDTO>();
		
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public UserDTO loadUser(String username) {
		try{		
			selectUserStatement.setString(1, username);
			ResultSet rs = selectUserStatement.executeQuery();
			while (rs.next()) {
				return new UserDTO(
						rs.getString(1), 	// userName
						rs.getString(2), 	// passhash
						rs.getString(3), 	// email
						rs.getString(4),	// name
						rs.getString(5));	//role 
			}
		}
		catch (SQLException e) {
			System.out.println("Exception retrieving user: " + e.getMessage());
		}
		return new UserDTO();
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public String saveUser(UserDTO user) {
		try{
			if (!user.isNewUser()) {
				updateUserStatement.setString(1, user.getPasshash());
				updateUserStatement.setString(2, user.getEmail());
				updateUserStatement.setString(3, user.getName());
				updateUserStatement.setString(4, user.getRole());
				updateUserStatement.setString(5, user.getUsername());
				updateUserStatement.executeUpdate();
				return user.getUsername();
			}
			else {
				//
				insertUserStatement.setString(1, user.getUsername());
				insertUserStatement.setString(2, user.getPasshash());
				insertUserStatement.setString(3, user.getEmail());
				insertUserStatement.setString(4, user.getName());
				insertUserStatement.setString(5, user.getRole());
				insertUserStatement.executeUpdate();
				return user.getUsername();
			}
		}
		catch (SQLException e) {
			System.out.println("Exception saving user: " + e.getMessage());
		}
		return "";
	}

	
}
