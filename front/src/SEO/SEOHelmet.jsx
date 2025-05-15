import React from 'react';
import { Helmet } from 'react-helmet-async';

const SEOHelmet = ({
    title,
    description,
    keywords,
    ogImage = '/path-to-default-social-image.jpg',
    canonical
}) => {
    const siteTitle = title ? `${title} | Your Donation Platform` : 'Your Donation Platform - Connecting Donors to Those in Need';
    const metaDescription = description || 'Help those in need by donating food, time, or resources through our platform that connects donors with people requiring assistance.';
    const metaKeywords = keywords || 'donation, charity, food donation, help, volunteer, community support';
    const canonicalLink = canonical || window.location.href;

    return (
        <Helmet>
            {/* Basic Meta Tags */}
            <title>{siteTitle}</title>
            <meta name="description" content={metaDescription} />
            <meta name="keywords" content={metaKeywords} />

            {/* Canonical URL */}
            <link rel="canonical" href={canonicalLink} />

            {/* Open Graph / Facebook */}
            <meta property="og:type" content="website" />
            <meta property="og:url" content={canonicalLink} />
            <meta property="og:title" content={siteTitle} />
            <meta property="og:description" content={metaDescription} />
            <meta property="og:image" content={ogImage} />

            {/* Twitter */}
            <meta name="twitter:card" content="summary_large_image" />
            <meta name="twitter:title" content={siteTitle} />
            <meta name="twitter:description" content={metaDescription} />
            <meta name="twitter:image" content={ogImage} />
        </Helmet>
    );
};

export default SEOHelmet;