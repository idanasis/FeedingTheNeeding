import React from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import client1 from "../Home/images/avatar.webp";
import cook from "../Home/images/dish.svg";
import GoSocial from "../Home/images/GoSocial.svg";
import GoCar from "../Home/images/GoCar.svg";

const GoManager = () => {
  const navigate = useNavigate();

  const managers = [
    {
      name: "בישול",
      post: "",
      image: cook,
      pages: [
        { name: "בקשות חוסים", path: "/social" },
        { name: "ניהול בישולים", path: "/cookManager" },
      ]
    },
    {
      name: "קהילה",
      post: "",
      image: GoSocial,
      pages: [
        { name: "החוסים", path: "/needers" },
        { name: "חוסים ממתינים", path: "/neederPending" },
        { name: "המתנדבים", path: "/donors" },
        { name: "מתנדבים ממתינים", path: "/donorPending" },
      ]
    },
    {
      name: "נסיעות",
      post: "",
      image: GoCar,
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
                <div className="p-1 rounded-full bg-red-500">
                  <div className="bg-white rounded-full p-1">
                    <img
                      src={manager.image}
                      alt={manager.name}
                      className="w-[60px] h-[60px] rounded-full object-cover"
                    />
                  </div>
                </div>
                <h1 className="text-white text-[22px] font-semibold uppercase">
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