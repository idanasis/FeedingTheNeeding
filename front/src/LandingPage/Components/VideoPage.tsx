// src/Components/VideoPage.tsx
import React from 'react';
import '../Styles/VideoPage.css';

const VideoPage: React.FC = () => {
    // ודא שהנתיב לקובץ הווידאו כאן נכון והקובץ קיים ב- public/videos/
    const videoSrc = '../Images/logo.png';

    return (
        <div className="video-page-container">
            <video className="video-player" controls autoPlay playsInline>
                <source src={videoSrc} type="video/mp4" />
                הדפדפן שלך אינו תומך בתג הווידאו.
            </video>
        </div>
    );
};

export default VideoPage;