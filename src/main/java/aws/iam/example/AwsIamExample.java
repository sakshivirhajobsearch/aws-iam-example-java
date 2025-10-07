package aws.iam.example;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachUserPolicyRequest;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.CreateUserRequest;
import software.amazon.awssdk.services.iam.model.DeleteUserRequest;
import software.amazon.awssdk.services.iam.model.DetachUserPolicyRequest;
import software.amazon.awssdk.services.iam.model.EntityAlreadyExistsException;
import software.amazon.awssdk.services.iam.model.ListAttachedUserPoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListAttachedUserPoliciesResponse;
import software.amazon.awssdk.services.iam.model.ListUsersResponse;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.User;

public class AwsIamExample {

    private static final String IAM_USER = "demo-java-iam-user";

    public static void main(String[] args) {
    	
        IamClient iam = IamClient.builder()
                .region(Region.AWS_GLOBAL) // IAM is a global service
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        listUsers(iam);
        createUser(iam, IAM_USER);
        attachPolicy(iam, IAM_USER, "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess");
        deleteUser(iam, IAM_USER);

        iam.close();
    }

    public static void listUsers(IamClient iam) {
    	
        System.out.println("=== Listing IAM Users ===");
        ListUsersResponse response = iam.listUsers();
        for (User user : response.users()) {
            System.out.println("- " + user.userName());
        }
    }

    public static void createUser(IamClient iam, String username) {
    	
        System.out.println("\n=== Creating IAM User: " + username + " ===");
        try {
            CreateUserRequest request = CreateUserRequest.builder().userName(username).build();
            iam.createUser(request);
            System.out.println("User created: " + username);
        } catch (EntityAlreadyExistsException e) {
            System.out.println("User already exists: " + username);
        }
    }

    public static void attachPolicy(IamClient iam, String username, String policyArn) {
    	
        System.out.println("\n=== Attaching Policy to User ===");
        AttachUserPolicyRequest attachRequest = AttachUserPolicyRequest.builder()
                .userName(username)
                .policyArn(policyArn)
                .build();
        iam.attachUserPolicy(attachRequest);
        System.out.println("Attached policy: " + policyArn + " to user: " + username);
    }

    public static void deleteUser(IamClient iam, String username) {
    	
        System.out.println("\n=== Deleting IAM User: " + username + " ===");
        try {
            // Detach policies before deleting
            ListAttachedUserPoliciesResponse policyResponse = iam.listAttachedUserPolicies(
                    ListAttachedUserPoliciesRequest.builder().userName(username).build());
            for (AttachedPolicy policy : policyResponse.attachedPolicies()) {
                iam.detachUserPolicy(
                        DetachUserPolicyRequest.builder()
                                .userName(username)
                                .policyArn(policy.policyArn())
                                .build());
                System.out.println("Detached policy: " + policy.policyName());
            }

            iam.deleteUser(DeleteUserRequest.builder().userName(username).build());
            System.out.println("Deleted user: " + username);
        } catch (NoSuchEntityException e) {
            System.out.println("User does not exist: " + username);
        }
    }
}
