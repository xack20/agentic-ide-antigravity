/**
 * Email sending options
 */
export interface IEmailOptions {
    to: string;
    subject: string;
    text?: string;
    html?: string;
}

/**
 * Email service interface
 */
export interface IEmailService {
    sendEmail(options: IEmailOptions): Promise<void>;
    sendWelcomeEmail(to: string, userName: string): Promise<void>;
}
