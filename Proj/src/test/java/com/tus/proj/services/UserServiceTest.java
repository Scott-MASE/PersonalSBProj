package com.tus.proj.services;

import com.tus.proj.note_managment.NoteRepository;
import com.tus.proj.service.UserService;
import com.tus.proj.user_managment.User;
import com.tus.proj.user_managment.UserRepository;
import com.tus.proj.user_managment.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private NoteRepository noteRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		// Create dummy users (use IDs other than 1L since ID 1 is reserved as system
		// admin)
		user1 = new User();
		user1.setId(2L);
		user1.setUsername("user1");
		user1.setPassword("rawPassword1");
		user1.setRole(UserRole.USER);

		user2 = new User();
		user2.setId(3L);
		user2.setUsername("user2");
		user2.setPassword("rawPassword2");
		user2.setRole(UserRole.ADMIN);
	}

	@Test
	 void testCreateUser() {
		when(passwordEncoder.encode("rawPassword1")).thenReturn("encodedPassword1");

		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			return invocation.getArgument(0);
		});

		// Act
		User created = userService.createUser(user1);

		assertEquals("encodedPassword1", created.getPassword());
		verify(passwordEncoder, times(1)).encode("rawPassword1");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	 void testGetAllUsers() {
		List<User> users = Arrays.asList(user1, user2);
		when(userRepository.findAll()).thenReturn(users);

		List<User> result = userService.getAllUsers();

		assertEquals(users, result);
	}

	@Test
	 void testGetUserById_Found() {
		when(userRepository.findById(2L)).thenReturn(Optional.of(user1));

		Optional<User> result = userService.getUserById(2L);

		assertTrue(result.isPresent());
		assertEquals(user1, result.get());
	}

	@Test
	 void testGetUserById_NotFound() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		Optional<User> result = userService.getUserById(999L);

		assertFalse(result.isPresent());
	}

	@Test
	 void testDeleteUser_Success() {
		when(userRepository.findById(2L)).thenReturn(Optional.of(user1));
		doNothing().when(noteRepository).deleteByUserId(2L);
		doNothing().when(userRepository).deleteById(2L);

		boolean deleted = userService.deleteUser(2L);

		assertTrue(deleted);
		verify(noteRepository, times(1)).deleteByUserId(2L);
		verify(userRepository, times(1)).deleteById(2L);
	}

	@Test
	 void testDeleteUser_NotFound() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		boolean deleted = userService.deleteUser(999L);

		assertFalse(deleted);
		verify(noteRepository, never()).deleteByUserId(any());
	}

	@Test
	 void testDeleteUser_SystemAdminDeletion() {

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			userService.deleteUser(1L);
		});
		assertEquals("Cannot delete the user with ID 1 (sys admin)", exception.getMessage());
	}

	@Test
	 void testAuthenticate_Success() {

		when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
		when(passwordEncoder.matches("rawPassword1", user1.getPassword())).thenReturn(true);

		User authUser = userService.authenticate("user1", "rawPassword1");

		assertEquals(user1, authUser);
	}

	@Test
	 void testAuthenticate_Failure() {

		when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

		when(passwordEncoder.matches("wrongPassword", user1.getPassword())).thenReturn(false);

		BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
			userService.authenticate("user1", "wrongPassword");
		});
		assertEquals("Invalid username or password", exception.getMessage());
	}

	@Test
	 void testEditUser_Success() {

		when(userRepository.findById(2L)).thenReturn(Optional.of(user1));

		when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

		User updatedUser = new User();
		updatedUser.setId(2L);
		updatedUser.setUsername("newUsername");
		updatedUser.setPassword("newEncodedPassword");
		updatedUser.setRole(UserRole.ADMIN);

		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		Optional<User> result = userService.editUser(2L, "newUsername", "newPassword", UserRole.ADMIN);

		assertTrue(result.isPresent());
		assertEquals("newUsername", result.get().getUsername());
		assertEquals("newEncodedPassword", result.get().getPassword());
		assertEquals(UserRole.ADMIN, result.get().getRole());
		verify(passwordEncoder, times(1)).encode("newPassword");
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	 void testEditUser_NotFound() {

		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		Optional<User> result = userService.editUser(999L, "newUsername", "newPassword", UserRole.ADMIN);

		assertFalse(result.isPresent());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	 void testFindByUsername_Found() {
		when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));

		Optional<User> result = userService.findByUsername("user1");

		assertTrue(result.isPresent());
		assertEquals(user1, result.get());
	}

	@Test
	 void testFindByUsername_NotFound() {
		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

		Optional<User> result = userService.findByUsername("unknown");

		assertFalse(result.isPresent());
	}

	@Test
	 void testExistsById_True() {
		when(userRepository.existsById(2L)).thenReturn(true);

		boolean exists = userService.existsById(2L);

		assertTrue(exists);
	}

	@Test
	 void testExistsById_False() {
		when(userRepository.existsById(999L)).thenReturn(false);

		boolean exists = userService.existsById(999L);

		assertFalse(exists);
	}
}