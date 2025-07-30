package aws.iam.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachUserPolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachUserPolicyResponse;
import software.amazon.awssdk.services.iam.model.CreateUserRequest;
import software.amazon.awssdk.services.iam.model.CreateUserResponse;
import software.amazon.awssdk.services.iam.model.DeleteUserRequest;
import software.amazon.awssdk.services.iam.model.DeleteUserResponse;

public class AwsIamExampleTest {

	private IamClient mockIamClient;

	@BeforeEach
	public void setup() {
		mockIamClient = Mockito.mock(IamClient.class);
	}

	@Test
	public void testCreateUser() {
		CreateUserResponse response = CreateUserResponse.builder().user(u -> u.userName("test-user")).build();

		when(mockIamClient.createUser(any(CreateUserRequest.class))).thenReturn(response);

		CreateUserRequest request = CreateUserRequest.builder().userName("test-user").build();

		CreateUserResponse result = mockIamClient.createUser(request);

		assertEquals("test-user", result.user().userName());
	}

	@Test
	public void testAttachPolicy() {
		AttachUserPolicyResponse response = AttachUserPolicyResponse.builder().build();

		when(mockIamClient.attachUserPolicy(any(AttachUserPolicyRequest.class))).thenReturn(response);

		AttachUserPolicyRequest request = AttachUserPolicyRequest.builder().userName("test-user")
				.policyArn("arn:aws:iam::aws:policy/ReadOnlyAccess").build();

		AttachUserPolicyResponse result = mockIamClient.attachUserPolicy(request);

		// Optional: assert no exception or validate something
		assertEquals(response, result);
	}

	@Test
	public void testDeleteUser() {
		DeleteUserResponse response = DeleteUserResponse.builder().build();

		when(mockIamClient.deleteUser(any(DeleteUserRequest.class))).thenReturn(response);

		DeleteUserRequest request = DeleteUserRequest.builder().userName("test-user").build();

		DeleteUserResponse result = mockIamClient.deleteUser(request);

		// Optional: assert no exception or validate something
		assertEquals(response, result);
	}
}
