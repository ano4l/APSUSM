package com.apsusm.service;

import com.apsusm.model.Member;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class CardGenerationService {

    private static final Logger log = LoggerFactory.getLogger(CardGenerationService.class);

    // Card dimensions (standard CR80 card at 300 DPI)
    private static final int CARD_WIDTH = 1012;
    private static final int CARD_HEIGHT = 638;
    private static final int CORNER_RADIUS = 30;

    // Brand colors matching the APSUSM design
    private static final Color BG_DARK = new Color(10, 10, 10);
    private static final Color TEXT_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_GRAY = new Color(156, 163, 175);
    private static final Color TEXT_LIGHT_GRAY = new Color(107, 114, 128);
    private static final Color BRAND_BLUE = new Color(0, 122, 204);
    private static final Color BRAND_RED = new Color(215, 25, 32);
    private static final Color BRAND_GREEN = new Color(139, 197, 63);
    private static final Color BRAND_PURPLE = new Color(102, 45, 145);
    private static final Color CHIP_LIGHT = new Color(200, 200, 210);
    private static final Color CHIP_DARK = new Color(160, 160, 175);
    private static final Color PHOTO_BG = new Color(30, 30, 40);
    private static final Color GRID_LINE = new Color(255, 255, 255, 8);

    // Back card colors
    private static final Color BACK_BG = new Color(245, 245, 250);
    private static final Color BACK_TEXT_DARK = new Color(30, 30, 50);
    private static final Color BACK_TEXT_GRAY = new Color(100, 100, 120);
    private static final Color BACK_BORDER = new Color(220, 220, 230);

    @Value("${app.cards.dir}")
    private String cardsDir;

    /**
     * Generate both front and back of the membership card.
     * Returns paths: [frontPath, backPath]
     */
    public String[] generateCard(Member member) throws Exception {
        log.info("Generating card for member: {}", member.getMemberId());

        BufferedImage frontCard = generateFrontCard(member);
        BufferedImage backCard = generateBackCard(member);

        String frontFilename = member.getMemberId().replace("-", "_") + "_front.png";
        String backFilename = member.getMemberId().replace("-", "_") + "_back.png";

        Path frontPath = Paths.get(cardsDir, frontFilename);
        Path backPath = Paths.get(cardsDir, backFilename);

        ImageIO.write(frontCard, "PNG", frontPath.toFile());
        ImageIO.write(backCard, "PNG", backPath.toFile());

        log.info("Card generated successfully: {} and {}", frontPath, backPath);
        return new String[]{frontPath.toString(), backPath.toString()};
    }

    private BufferedImage generateFrontCard(Member member) throws Exception {
        BufferedImage card = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = card.createGraphics();
        enableAntialiasing(g);

        // Background with rounded corners
        drawRoundedRect(g, 0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, BG_DARK);

        // Subtle tech grid pattern
        g.setColor(GRID_LINE);
        for (int x = 0; x < CARD_WIDTH; x += 20) {
            g.drawLine(x, 0, x, CARD_HEIGHT);
        }
        for (int y = 0; y < CARD_HEIGHT; y += 20) {
            g.drawLine(0, y, CARD_WIDTH, y);
        }

        // Smart card chip (top-right)
        drawChip(g, CARD_WIDTH - 140, 50);

        // APSUSM Logo area (top-left)
        drawLogo(g, 50, 50);

        // "APSUSM" text
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(TEXT_WHITE);
        g.drawString("APSUSM", 100, 70);

        // "VERIFIED MEMBER" subtitle
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(TEXT_GRAY);
        g.drawString("VERIFIED MEMBER", 100, 90);

        // Portrait photo
        drawPortrait(g, member, 50, 160, 150, 190);

        // Member name
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(TEXT_WHITE);
        String fullName = member.getFullName();
        if (fullName.length() > 28) fullName = fullName.substring(0, 25) + "...";
        g.drawString(fullName, 220, 220);

        // Specialization with blue dot
        g.setColor(BRAND_BLUE);
        g.fillOval(220, 245, 10, 10);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(TEXT_GRAY);
        String spec = member.getSpecialization() != null ? member.getSpecialization() : "Medical Professional";
        g.drawString(spec, 236, 256);

        // License number
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g.setColor(TEXT_LIGHT_GRAY);
        g.drawString("LIC: " + member.getLicenseNumber(), 220, 290);

        // Institution
        if (member.getInstitution() != null && !member.getInstitution().isEmpty()) {
            g.drawString(member.getInstitution(), 220, 315);
        }

        // Member ID (bottom left)
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        g.setColor(TEXT_WHITE);
        g.drawString("ID: " + member.getMemberId(), 50, CARD_HEIGHT - 80);

        // Province
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(TEXT_GRAY);
        g.drawString(member.getProvince() != null ? member.getProvince() : "", 50, CARD_HEIGHT - 55);

        // Expiry date (bottom right)
        String expiry = member.getExpiresAt() != null
                ? member.getExpiresAt().format(DateTimeFormatter.ofPattern("MM/yyyy"))
                : LocalDateTime.now().plusYears(1).format(DateTimeFormatter.ofPattern("MM/yyyy"));
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g.setColor(TEXT_LIGHT_GRAY);
        g.drawString("EXP: " + expiry, CARD_WIDTH - 200, CARD_HEIGHT - 80);

        // Issue date
        String issued = member.getPaidAt() != null
                ? member.getPaidAt().format(DateTimeFormatter.ofPattern("MM/yyyy"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        g.drawString("ISS: " + issued, CARD_WIDTH - 200, CARD_HEIGHT - 55);

        // Bottom gradient line (blue -> red -> green)
        GradientPaint bottomGradient = new GradientPaint(0, 0, BRAND_BLUE, CARD_WIDTH / 2f, 0, BRAND_RED);
        g.setPaint(bottomGradient);
        g.fillRoundRect(0, CARD_HEIGHT - 8, CARD_WIDTH / 2, 8, 0, 0);

        GradientPaint bottomGradient2 = new GradientPaint(CARD_WIDTH / 2f, 0, BRAND_RED, CARD_WIDTH, 0, BRAND_GREEN);
        g.setPaint(bottomGradient2);
        g.fillRoundRect(CARD_WIDTH / 2, CARD_HEIGHT - 8, CARD_WIDTH / 2, 8, 0, 0);

        g.dispose();
        return card;
    }

    private BufferedImage generateBackCard(Member member) throws Exception {
        BufferedImage card = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = card.createGraphics();
        enableAntialiasing(g);

        // White/light background
        drawRoundedRect(g, 0, 0, CARD_WIDTH, CARD_HEIGHT, CORNER_RADIUS, BACK_BG);

        // Top accent bar
        GradientPaint topGradient = new GradientPaint(0, 0, BRAND_PURPLE, CARD_WIDTH, 0, BRAND_BLUE);
        g.setPaint(topGradient);
        g.fillRect(0, 0, CARD_WIDTH, 6);

        // QR Code (left side)
        BufferedImage qrCode = generateQRCode(
                "https://apsusm.org/verify/" + member.getMemberId(),
                200
        );
        if (qrCode != null) {
            g.drawImage(qrCode, 50, 60, 200, 200, null);
        }

        // Organization details (right of QR)
        int textX = 280;
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("APSUSM", textX, 90);

        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.setColor(BACK_TEXT_GRAY);
        g.drawString("Associação dos Profissionais de Saúde", textX, 115);
        g.drawString("Unidos e Solidários de Moçambique", textX, 130);

        // Divider
        g.setColor(BACK_BORDER);
        g.drawLine(textX, 150, CARD_WIDTH - 50, 150);

        // Member details
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("MEMBER", textX, 175);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(BACK_TEXT_GRAY);
        g.drawString(member.getFullName(), textX, 195);

        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("MEMBER ID", textX, 225);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setColor(BACK_TEXT_GRAY);
        g.drawString(member.getMemberId(), textX, 245);

        // Issue and expiry
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("ISSUED", textX + 350, 175);

        String issued = member.getPaidAt() != null
                ? member.getPaidAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(BACK_TEXT_GRAY);
        g.drawString(issued, textX + 350, 195);

        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("EXPIRES", textX + 350, 225);

        String expiry = member.getExpiresAt() != null
                ? member.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : LocalDateTime.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(BACK_TEXT_GRAY);
        g.drawString(expiry, textX + 350, 245);

        // Security notice
        g.setColor(BACK_BORDER);
        g.drawLine(50, CARD_HEIGHT - 200, CARD_WIDTH - 50, CARD_HEIGHT - 200);

        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.setColor(BACK_TEXT_GRAY);
        String[] securityText = {
                "This card certifies the bearer as a verified member of APSUSM.",
                "Membership is subject to annual renewal and compliance with the APSUSM Code of Ethics.",
                "Unauthorized reproduction or alteration of this card is strictly prohibited.",
                "To verify this membership, scan the QR code or visit: apsusm.org/verify"
        };
        int secY = CARD_HEIGHT - 175;
        for (String line : securityText) {
            g.drawString(line, 50, secY);
            secY += 18;
        }

        // Contact info at bottom
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(BACK_TEXT_DARK);
        g.drawString("Contact: info@apsusm.org  |  www.apsusm.org  |  Maputo, Moçambique", 50, CARD_HEIGHT - 50);

        // Bottom gradient line
        GradientPaint bottomGradient = new GradientPaint(0, 0, BRAND_BLUE, CARD_WIDTH, 0, BRAND_GREEN);
        g.setPaint(bottomGradient);
        g.fillRect(0, CARD_HEIGHT - 6, CARD_WIDTH, 6);

        g.dispose();
        return card;
    }

    private void drawPortrait(Graphics2D g, Member member, int x, int y, int w, int h) {
        // Photo border
        g.setColor(new Color(50, 50, 60));
        g.fillRoundRect(x - 2, y - 2, w + 4, h + 4, 10, 10);

        // Photo background
        g.setColor(PHOTO_BG);
        g.fillRoundRect(x, y, w, h, 8, 8);

        // Try to load and draw the actual photo
        if (member.getPhotoPath() != null) {
            try {
                BufferedImage photo = ImageIO.read(new File(member.getPhotoPath()));
                if (photo != null) {
                    // Scale and crop to fit
                    BufferedImage scaled = scaleAndCrop(photo, w, h);

                    // Clip to rounded rect
                    Shape clip = g.getClip();
                    g.setClip(new RoundRectangle2D.Float(x, y, w, h, 8, 8));
                    g.drawImage(scaled, x, y, w, h, null);
                    g.setClip(clip);
                    return;
                }
            } catch (IOException e) {
                log.warn("Could not load photo: {}", member.getPhotoPath(), e);
            }
        }

        // Placeholder silhouette if no photo
        g.setColor(TEXT_LIGHT_GRAY);
        g.fillOval(x + w / 2 - 25, y + 30, 50, 50);
        g.fillRoundRect(x + w / 2 - 35, y + 90, 70, 60, 20, 20);
    }

    private BufferedImage scaleAndCrop(BufferedImage source, int targetW, int targetH) {
        double sourceRatio = (double) source.getWidth() / source.getHeight();
        double targetRatio = (double) targetW / targetH;

        int cropW, cropH, cropX, cropY;
        if (sourceRatio > targetRatio) {
            cropH = source.getHeight();
            cropW = (int) (cropH * targetRatio);
            cropX = (source.getWidth() - cropW) / 2;
            cropY = 0;
        } else {
            cropW = source.getWidth();
            cropH = (int) (cropW / targetRatio);
            cropX = 0;
            cropY = (source.getHeight() - cropH) / 2;
        }

        BufferedImage cropped = source.getSubimage(cropX, cropY, cropW, cropH);
        BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        enableAntialiasing(g);
        g.drawImage(cropped, 0, 0, targetW, targetH, null);
        g.dispose();
        return scaled;
    }

    private void drawLogo(Graphics2D g, int x, int y) {
        // Simplified APSUSM logo - cross with leaf
        int size = 36;

        // Ring arc
        GradientPaint ringGrad = new GradientPaint(x, y, BRAND_PURPLE, x + size, y, BRAND_RED);
        g.setPaint(ringGrad);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x, y, size, size, -30, -120);

        // Red cross
        g.setColor(BRAND_RED);
        int cx = x + size / 2;
        int cy = y + size / 2;
        g.fillRect(cx - 7, cy - 2, 14, 4);
        g.fillRect(cx - 2, cy - 7, 4, 14);

        // Blue and green stethoscope arcs
        g.setColor(BRAND_BLUE);
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x + 2, y + 2, size / 2, size / 2, 60, 120);

        g.setColor(BRAND_GREEN);
        g.drawArc(x + size / 2 - 2, y + 2, size / 2, size / 2, 0, 120);

        g.setStroke(new BasicStroke(1));
    }

    private void drawChip(Graphics2D g, int x, int y) {
        int w = 55;
        int h = 40;

        // Chip body
        GradientPaint chipGrad = new GradientPaint(x, y, CHIP_LIGHT, x + w, y + h, CHIP_DARK);
        g.setPaint(chipGrad);
        g.fillRoundRect(x, y, w, h, 6, 6);

        // Chip border
        g.setColor(new Color(140, 140, 155));
        g.drawRoundRect(x, y, w, h, 6, 6);

        // Chip internal lines
        g.setColor(new Color(140, 140, 155, 100));
        g.drawLine(x + w / 2, y + 4, x + w / 2, y + h - 4);
        g.drawLine(x + 4, y + h / 2, x + w - 4, y + h / 2);
    }

    private void drawRoundedRect(Graphics2D g, int x, int y, int w, int h, int r, Color color) {
        g.setColor(color);
        g.fill(new RoundRectangle2D.Float(x, y, w, h, r, r));
    }

    private BufferedImage generateQRCode(String text, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int ix = 0; ix < size; ix++) {
                for (int iy = 0; iy < size; iy++) {
                    image.setRGB(ix, iy, matrix.get(ix, iy) ? 0xFF1A1A2E : 0xFFF5F5FA);
                }
            }
            return image;
        } catch (Exception e) {
            log.error("QR code generation failed", e);
            return null;
        }
    }

    private void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
}
