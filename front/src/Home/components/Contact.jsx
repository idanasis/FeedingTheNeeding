import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { slideUpVariants, zoomInVariants } from './animation';
import { createOrUpdateNeedy } from '../homeApi'; // Import the API function

const BEER_SHEVA_STREETS = [
  'העיר העתיקה',
  'נווה עופר',
  'המרכז האזרחי',
  'א\'',
  'ב\'',
  'ג\'',
  'ד\'',
  'ה\'',
  'ו\'',
  'ט\'',
  'י"א',
  'נאות לון',
  'נווה זאב',
  'נווה נוי',
  'נחל בקע',
  'נחל עשן (נווה מנחם)',
  'רמות',
  'נאות אברהם (פלח 6)',
  'נווה אילן (פלח 7)',
  'הכלניות',
  'סיגליות',
  'פארק הנחל'
];

const Contact = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    address: '',
    street: '', // New field for street/neighborhood
    phoneNumber: '',
    familySize: 0,
    notes: '',
  });

  const [errors, setErrors] = useState({
    firstName: '',
    lastName: '',
    address: '',
    street: '', // New error state for street
    phoneNumber: '',
    familySize: '',
  });

  const [touched, setTouched] = useState({
    firstName: false,
    lastName: false,
    address: false,
    street: false, // New touched state for street
    phoneNumber: false,
    familySize: false,
  });

  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  // Validate individual field
  const validateField = (name, value) => {
    let error = '';
    
    switch (name) {
      case 'firstName':
        error = !value.trim() ? 'שם פרטי הוא שדה חובה' : '';
        break;
      case 'lastName':
        error = !value.trim() ? 'שם משפחה הוא שדה חובה' : '';
        break;
      case 'address':
        error = !value.trim() ? 'כתובת היא שדה חובה' : '';
        break;
      case 'street':
        error = !value.trim() ? 'שכונה/רחוב הוא שדה חובה' : '';
        break;
      case 'phoneNumber':
        if (!value.trim()) {
          error = 'מספר טלפון הוא שדה חובה';
        } else if (!/^\d+$/.test(value)) {
          error = 'מספר טלפון חייב להכיל ספרות בלבד';
        } else if (value.length !== 10) {
          error = 'מספר טלפון חייב להיות באורך של 10 ספרות';
        }
        break;
      case 'familySize':
        if (!value) {
          error = 'מספר נפשות הוא שדה חובה';
        } else if (value <= 0) {
          error = 'מספר נפשות חייב להיות גדול מ-0';
        }
        break;
      default:
        break;
    }
    
    return error;
  };

  // Validate all form fields
  const validateForm = () => {
    const newErrors = {
      firstName: validateField('firstName', formData.firstName),
      lastName: validateField('lastName', formData.lastName),
      address: validateField('address', formData.address),
      street: validateField('street', formData.street), // Validate street
      phoneNumber: validateField('phoneNumber', formData.phoneNumber),
      familySize: validateField('familySize', formData.familySize),
    };
    
    setErrors(newErrors);
    return !Object.values(newErrors).some(error => error);
  };

  // Handle input changes with real-time validation
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    const newValue = name === 'familySize' ? Number(value) || 0 : value;
    
    setFormData(prev => ({
      ...prev,
      [name]: newValue,
    }));
    
    // Real-time validation for touched fields
    if (touched[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: validateField(name, newValue)
      }));
    }
  };

  // Mark field as touched when focus is lost
  const handleBlur = (e) => {
    const { name } = e.target;
    
    setTouched(prev => ({
      ...prev,
      [name]: true
    }));
    
    setErrors(prev => ({
      ...prev,
      [name]: validateField(name, formData[name])
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccessMessage('');
    setErrorMessage('');

    // Mark all fields as touched
    const allTouched = Object.keys(touched).reduce((acc, key) => {
      acc[key] = true;
      return acc;
    }, {});
    setTouched(allTouched);

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await createOrUpdateNeedy({
        ...formData,
        city: 'Beer-Sheva', // Add default value
        role: 'NEEDY', // Default role
        confirmStatus: 'PENDING', // Default status
      });
      setSuccessMessage('בקשה נשלחה בהצלחה!');
      setFormData({
        firstName: '',
        lastName: '',
        address: '',
        street: '', // Reset street
        phoneNumber: '',
        familySize: 0,
        notes: '',
      });
      // Reset touched states after successful submission
      setTouched({
        firstName: false,
        lastName: false,
        address: false,
        street: false, // Reset street touch
        phoneNumber: false,
        familySize: false,
      });
    } catch (error) {
      setErrorMessage('שגיאה בשליחת הבקשה. נסה שוב.');
      console.error('Error submitting form:', error);
    } finally {
      setLoading(false);
    }
  };

  // Helper function to determine input class based on error state
  const getInputClass = (fieldName) => {
    const baseClass = "px-6 py-3 border-[2px] text-black rounded-lg w-full";
    
    if (!touched[fieldName]) return `${baseClass} border-black`;
    return errors[fieldName] 
      ? `${baseClass} border-red-500` 
      : `${baseClass} border-green-500`;
  };

  return (
    <div id="בקשת סיוע">
      <div className="lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between">
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={slideUpVariants}
          className="lg:w-[60%] w-full flex flex-col justify-center items-start gap-6"
        >
          <motion.h1 variants={slideUpVariants} className="text-red-500 text-3xl">
            לבקשת סיוע
          </motion.h1>
          <motion.h1 variants={slideUpVariants} className="text-white uppercase text-[40px] font-bold">
            אנחנו כאן לעזור
          </motion.h1>
          <div className="w-[120px] h-[6px] bg-red-500"></div>
          <p className="text-3xl italic text-red-100 mt-[60px]">
            אם אתם מתקשים כלכלית ורוצים עזרה מהעמותה שלנו, זה המקום לבקש. מי שבמקש יכנס לרשימת המתנה, במידה ויהיה מקום וימצא מתאים ניצור איתכם קשר.
          </p>
        </motion.div>
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={slideUpVariants}
          className="lg:w-[40%] w-full flex flex-col justify-center items-start gap-6"
        >
          <motion.form
            initial="hidden"
            whileInView="visible"
            variants={zoomInVariants}
            className="flex flex-col justify-center items-start gap-4 w-full"
            onSubmit={handleSubmit}
          >
            <div className="w-full">
              <input
                type="text"
                name="firstName"
                placeholder="הכנס שם פרטי"
                value={formData.firstName}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('firstName')}
              />
              {touched.firstName && errors.firstName && (
                <p className="text-red-500 text-sm mt-1">{errors.firstName}</p>
              )}
            </div>
            
            <div className="w-full">
              <input
                type="text"
                name="lastName"
                placeholder="הכנס שם משפחה"
                value={formData.lastName}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('lastName')}
              />
              {touched.lastName && errors.lastName && (
                <p className="text-red-500 text-sm mt-1">{errors.lastName}</p>
              )}
            </div>
              <div className="w-full">
              <select
                name="street"
                value={formData.street}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('street')}
              >
                <option value="">בחר שכונה/רחוב</option>
                {BEER_SHEVA_STREETS.map((street) => (
                  <option key={street} value={street}>
                    {street}
                  </option>
                ))}
              </select>
              {touched.street && errors.street && (
                <p className="text-red-500 text-sm mt-1">{errors.street}</p>
              )}
            </div>
            
            <div className="w-full">
              <input
                type="text"
                name="address"
                placeholder="הכנס כתובת מגורים"
                value={formData.address}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('address')}
              />
              {touched.address && errors.address && (
                <p className="text-red-500 text-sm mt-1">{errors.address}</p>
              )}
            </div>
            
            <div className="w-full">
              <input
                type="tel"
                name="phoneNumber"
                placeholder="הכנס מספר טלפון (10 ספרות)"
                value={formData.phoneNumber}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('phoneNumber')}
              />
              {touched.phoneNumber && errors.phoneNumber && (
                <p className="text-red-500 text-sm mt-1">{errors.phoneNumber}</p>
              )}
            </div>
            
            <div className="w-full">
              <input
                type="number"
                name="familySize"
                placeholder="הכנס מספר נפשות"
                value={formData.familySize || ''}
                onChange={handleInputChange}
                onBlur={handleBlur}
                className={getInputClass('familySize')}
                min="1"
              />
              {touched.familySize && errors.familySize && (
                <p className="text-red-500 text-sm mt-1">{errors.familySize}</p>
              )}
            </div>
            
            <textarea
              name="notes"
              placeholder="הערות נוספות"
              value={formData.notes}
              onChange={handleInputChange}
              rows={4}
              className="px-6 py-3 border-[2px] border-black text-black rounded-lg w-full"
            ></textarea>
            
            <motion.button
              variants={zoomInVariants}
              type="submit"
              className="bg-red-500 hover:bg-black hover:text-white px-10 py-4 rounded-lg text-black font-bold"
              disabled={loading}
            >
              {loading ? 'שולח...' : 'שליחה'}
            </motion.button>
          </motion.form>
          {successMessage && <p className="text-green-500 mt-4">{successMessage}</p>}
          {errorMessage && <p className="text-red-500 mt-4">{errorMessage}</p>}
        </motion.div>
      </div>
    </div>
  );
};

export default Contact;