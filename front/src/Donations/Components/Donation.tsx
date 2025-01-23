import React, { useState } from 'react';
import '../Styles/Donation.css';

// Import images
import QRCodeImg from '../Images/QRcode.png';
import CreditCardImg from '../Images/CreditCardPhoto.png';
import PayBoxImg from '../Images/payboxPhoto.png';
import BitImg from '../Images/bitPhoto.png';
import DiveHeader from '../../GoPage/DiveHeader';


const DonationPage: React.FC = () => {
    const [selectedOption, setSelectedOption] = useState<string | null>(null);
    const [isQrCodeEnlarged, setIsQrCodeEnlarged] = useState<boolean>(false);
    const [clickTimeout, setClickTimeout] = useState<ReturnType<typeof setTimeout> | null>(null);

    const handleOptionClick = (option: string) => {
        setSelectedOption(option);
        setIsQrCodeEnlarged(false);
    };

    const handleQrCodeClick = () => {
        if (clickTimeout) {
            clearTimeout(clickTimeout);
            setClickTimeout(null);
            window.open("https://www.bitpay.co.il/app/me/64C912E7-D77C-F924-1DC3-AECC163BAE41FA3E", "_blank");
        } else {
            const timeout = setTimeout(() => {
                setIsQrCodeEnlarged((prev) => !prev);
                setClickTimeout(null);
            }, 250); // 250ms timeout to differentiate single and double clicks
            setClickTimeout(timeout);
        }
    };

    return (
        <>
            <DiveHeader />
        <div className="donation-page">
            <div className="donation-container">
                <div className="donation-header">
                    <h1>תרום עכשיו</h1>
                    <p>בחר/י את הדרך הנוחה לך לתרום ולעזור לנו להביא שינוי אמיתי.</p>
                </div>

                <div className="donation-content">
                    {selectedOption === 'bit' && (
                        <div className="donation-method bit-method">
                            <h2>תרומה באמצעות ביט</h2>
                            <p className="bit-instructions">אנא סרקו את הקוד או לחצו עליו לפי ההנחיות:</p>
                            <ul className="bit-guide">
                                <li><strong>לחיצה אחת:</strong> להגדלת הקוד לצפייה נוחה.</li>
                                <li><strong>לחיצה כפולה:</strong> מעבר ישיר לתרומה באפליקציית ביט.</li>
                            </ul>
                            <div
                                className={`qr-container ${isQrCodeEnlarged ? 'enlarged' : ''}`}
                                onClick={handleQrCodeClick}
                            >
                                <img src={QRCodeImg} alt="Bit QR Code" className="qr-code" />
                            </div>
                        </div>
                    )}

                    {selectedOption === 'paybox' && (
                        <div className="donation-method paybox-method">
                            <h2>תרומה באמצעות PayBox</h2>
                            <a
                                href="https://payboxapp.page.link/BdKay5zV7s66nLmw5"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="paybox-link"
                            >
                                לחצו כאן לתרומה ב-PayBox
                            </a>
                        </div>
                    )}

                    {selectedOption === 'creditcard' && (
                        <div className="donation-method creditcard-method">
                            <h2>תרומה באמצעות כרטיס אשראי</h2>
                            <p>בקרוב תוכלו לתרום בצורה מאובטחת באמצעות כרטיס אשראי!</p>
                            <p>כדי לתרום כעת, לחצו על אחת מהאופציות האחרות למטה.</p>
                        </div>
                    )}

                    {!selectedOption && (
                        <p className="instructions">לחצו על אחת מהאפשרויות למטה לבחירת דרך התרומה.</p>
                    )}
                </div>

                <div className="donation-options">
                    <div
                        className={`donation-option ${selectedOption === 'bit' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('bit')}
                    >
                        <img src={BitImg} alt="Bit Logo" />
                    </div>

                    <div
                        className={`donation-option ${selectedOption === 'paybox' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('paybox')}
                    >
                        <img src={PayBoxImg} alt="PayBox Logo" />
                    </div>

                    <div
                        className={`donation-option ${selectedOption === 'creditcard' ? 'selected' : ''}`}
                        onClick={() => handleOptionClick('creditcard')}
                    >
                        <img src={CreditCardImg} alt="Credit Card Logo" />
                    </div>
                </div>
            </div>
        </div>
            </>
    );
};

export default DonationPage;
