/**
 * Welcome email HTML template
 */
export function getWelcomeEmailTemplate(
    userName: string,
    appName: string = 'User Management'
): { subject: string; html: string; text: string } {
    const subject = `Welcome to ${appName}!`;

    const html = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
        .content { background: #f9fafb; padding: 30px; border-radius: 0 0 8px 8px; }
        .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin-top: 20px; }
        .footer { text-align: center; color: #6b7280; font-size: 12px; margin-top: 20px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Welcome, ${userName}!</h1>
    </div>
    <div class="content">
        <p>Thank you for registering with <strong>${appName}</strong>. Your account has been successfully created.</p>
        <p>You can now log in and start using our services.</p>
        <h3>What's next?</h3>
        <ul>
            <li>Complete your profile</li>
            <li>Explore available features</li>
            <li>Connect with other users</li>
        </ul>
        <p>If you have any questions, feel free to reach out to our support team.</p>
    </div>
    <div class="footer">
        <p>This email was sent by ${appName}. Please do not reply to this email.</p>
    </div>
</body>
</html>
    `.trim();

    const text = `
Welcome, ${userName}!

Thank you for registering with ${appName}. Your account has been successfully created.

You can now log in and start using our services.

What's next?
- Complete your profile
- Explore available features
- Connect with other users

If you have any questions, feel free to reach out to our support team.

This email was sent by ${appName}. Please do not reply to this email.
    `.trim();

    return { subject, html, text };
}
