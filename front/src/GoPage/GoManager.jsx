import React from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import client1 from "../Home/images/avatar.webp";

const GoManager = () => {
  const navigate = useNavigate();

  const managers = [
    {
      name: "שרה כהן",
      post: "רכזת מבשלות",
      image: client1,
      pages: [
        { name: "בלה בלה", path: "/info" },
        { name: "בלה בלה", path: "/calendar" },
        { name: "בלה בלה", path: "/tests" }
      ]
    },
    {
      name: "דוד לוי",
      post: "רכזת קהילה",
      image: client1,
      pages: [
        { name: "מעקב חוסים", path: "/social" },
        { name: "רשימת ממתינים", path: "/neederPending" },
        { name: "מתנדבים ממתינים", path: "/DonorPendingTable" }
      ]
    },
    {
      name: "מיכל רוזן",
      post: "רכזת נהגים",
      image: client1,
      pages: [
        { name: "ניהול נסיעות", path: "/driving" },
      ]
    }
  ];

  const slideUpVariants = {
    hidden: { opacity: 0, y: 50 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  const zoomInVariants = {
    hidden: { opacity: 0, scale: 0.8 },
    visible: { opacity: 1, scale: 1, transition: { duration: 0.5 } }
  };

  return (
    <div className="w-full">
      <motion.div
        initial="hidden"
        whileInView="visible"
        variants={slideUpVariants}
        className="lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]"
      >
        <motion.h1
          variants={slideUpVariants}
          className="text-white text-[40px] text-center font-bold"
        >
          ניהול
        </motion.h1>
        <motion.div
          className="w-[120px] h-[6px] bg-red-500"
          variants={slideUpVariants}
        ></motion.div>
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={zoomInVariants}
          className="lg:w-full w-[90%] grid lg:grid-cols-3 grid-cols-1 justify-center items-start gap-[30px] mt-[30px]"
        >
          {managers.map((manager, index) => (
            <div
              key={index}
              className="flex flex-col justify-between items-center border-2 border-white bg-opacity-10 bg-white p-4 w-full min-h-[400px]"
            >
              <div className="flex flex-col gap-2">
                {manager.pages.map((page, pageIndex) => (
                  <button
                    key={pageIndex}
                    onClick={() => navigate(page.path)}
                    className="text-white hover:text-red-500 text-lg text-center transition-colors duration-300 p-2 border border-transparent hover:border-red-500 rounded"
                  >
                    {page.name}
                  </button>
                ))}
              </div>
              <div className="flex flex-col justify-center items-center gap-[10px] mt-4">
                <img
                  src={manager.image}
                  alt={manager.name}
                  className="w-[80px] h-[80px] rounded-full"
                />
                <h1 className="text-white text-[27px] font-semibold uppercase">
                  {manager.name}
                </h1>
                <h1 className="text-red-500 text-[22px]">{manager.post}</h1>
              </div>
            </div>
          ))}
        </motion.div>
      </motion.div>
    </div>
  );
};

export default GoManager;
