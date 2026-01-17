import { injectable, inject } from 'tsyringe';
import nodemailer, { Transporter } from 'nodemailer';
import { IEmailService, IEmailOptions, IEmailConfig } from '@user-management/shared';
import { getWelcomeEmailTemplate } from '../templates/welcome-email.template';

/**
 * Email service implementation using Nodemailer
 */
@injectable()
export class EmailService implements IEmailService {
    private transporter: Transporter | null = null;

    constructor(
        @inject('IEmailConfig') private readonly config: IEmailConfig
    ) {
        this.initTransporter();
    }

    private initTransporter(): void {
        // Only create transporter if credentials are configured
        if (this.config.auth.user && this.config.auth.pass) {
            this.transporter = nodemailer.createTransport({
                host: this.config.host,
                port: this.config.port,
                secure: this.config.secure,
                auth: {
                    user: this.config.auth.user,
                    pass: this.config.auth.pass,
                },
            });
        } else {
            console.warn('[EmailService] Email credentials not configured. Emails will be logged only.');
        }
    }

    /**
     * Send an email
     */
    async sendEmail(options: IEmailOptions): Promise<void> {
        if (!this.transporter) {
            console.log('[EmailService] Would send email:', {
                to: options.to,
                subject: options.subject,
            });
            return;
        }

        try {
            const info = await this.transporter.sendMail({
                from: this.config.from,
                to: options.to,
                subject: options.subject,
                text: options.text,
                html: options.html,
            });
            console.log('[EmailService] Email sent:', info.messageId);
        } catch (error) {
            console.error('[EmailService] Failed to send email:', error);
            throw error;
        }
    }

    /**
     * Send welcome email to new user
     */
    async sendWelcomeEmail(to: string, userName: string): Promise<void> {
        const template = getWelcomeEmailTemplate(userName);
        await this.sendEmail({
            to,
            subject: template.subject,
            text: template.text,
            html: template.html,
        });
    }
}
