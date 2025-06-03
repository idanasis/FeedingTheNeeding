import React from 'react';
import PropTypes, { func, string } from 'prop-types';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import LoadingButton from '@mui/lab/LoadingButton';
import { NeederTrackingModel } from '@/src/commons/models/NeederTrackingModel';
import { useState } from 'react';
import { updateNeederTracking } from '../../commons/Restapi/socialRestapi';

function createData(neederTrackingModel: NeederTrackingModel) {
  return {
    id: neederTrackingModel.id,
    firstName: neederTrackingModel.needy.firstName,
    lastName: neederTrackingModel.needy.lastName,
    phoneNumber: neederTrackingModel.needy.phoneNumber,
    street: neederTrackingModel.needy.street || '', // Street/neighborhood field
    address: neederTrackingModel.needy.address,
    weekStatus: neederTrackingModel.weekStatus==="Here"?"זמין":"לא זמין",
    needyId: neederTrackingModel.needy.id,
    date: neederTrackingModel.date,
    details: [
      {
        familySize: neederTrackingModel.needy.familySize,
        foodPreference: neederTrackingModel.dietaryPreferences || "רגיל",
        notes: neederTrackingModel.additionalNotes || "ללא הערות נוספות",
      },
    ],
  };
}

const Row = (props: { row: ReturnType<typeof createData> }) =>  {
  const [row, setRow] = useState(props.row);
  const [open, setOpen] = React.useState(false);
  const [foodPreference, setFoodPreference] = useState(row.details[0].foodPreference || "רגיל");
  const [notes, setNotes] = useState(row.details[0].notes || "ללא הערות נוספות");
  const [inputsValid, setInputsValid] = useState(true);

  const handleFoodPreferenceChange = (event: React.ChangeEvent<HTMLInputElement>, id: number) => {
    const value = event.target.value;
    setFoodPreference(value);
    validateInputs(value, notes);
  }

  const handleNotesChange = (event: React.ChangeEvent<HTMLInputElement>, id: number) => {
    const value = event.target.value;
    setNotes(value);
    validateInputs(foodPreference, value);
  }

  const validateInputs = (food: string, note: string) => {
    setInputsValid(!!food.trim() && !!note.trim());
  }

  const handleAvailableChange = async (index: number, event: React.ChangeEvent<HTMLSelectElement>) => {
    try{
      const value = event.target.value;
      const neederTrackingModel: NeederTrackingModel = {
        id: row.id,
        date: row.date,
        needy: {
          id: row.needyId,
          firstName: row.firstName,
          lastName: row.lastName,
          phoneNumber: row.phoneNumber,
          street: row.street,
          address: row.address,
          familySize: row.details[0].familySize
        },
        weekStatus: value,
        dietaryPreferences: foodPreference,
        additionalNotes: notes
      };
      const res = await updateNeederTracking(index, neederTrackingModel);
      const updatedRow = { 
        ...row, 
        weekStatus: value,
        details: [{
          ...row.details[0],
          foodPreference: foodPreference,
          notes: notes
        }]
      };
      setRow(updatedRow);
      setOpen(false);
    } catch(err) {
      alert("שגיאה בעדכון סטטוס");
    }
  }

  const handleSave = async (index: number) => {
    try {
      if (!inputsValid) {
        alert("אנא מלא את כל השדות הנדרשים");
        return;
      }

      const neederTrackingModel: NeederTrackingModel = {
        id: row.id,
        date: row.date,
        needy: {
          id: row.needyId,
          firstName: row.firstName,
          lastName: row.lastName,
          phoneNumber: row.phoneNumber,
          street: row.street,
          address: row.address,
          familySize: row.details[0].familySize
        },
        weekStatus: row.weekStatus,
        dietaryPreferences: foodPreference,
        additionalNotes: notes
      };
      
      const res = await updateNeederTracking(index, neederTrackingModel);
      
      // Update the row state with new values
      const updatedRow = { 
        ...row, 
        details: [{
          ...row.details[0],
          foodPreference: foodPreference,
          notes: notes
        }]
      };
      setRow(updatedRow);
      
      alert("הפרטים נשמרו בהצלחה");
    } catch(err) {
      alert("שגיאה בעדכון פרטים");
    }
  }

  return (
    <React.Fragment>
      <TableRow sx={{ borderBottom: 'set' }}>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() =>{
              if(row.weekStatus==="זמין")
                setOpen(!open)
              else
                alert("לא ניתן לערוך פרטים של נזקק שאינו זמין")
            }}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell align="justify" component="th" scope="row" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
          {row.firstName} {row.lastName}
        </TableCell>
        <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' }, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
          {row.phoneNumber}
        </TableCell>
        <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
          {row.street}, {row.address} 
        </TableCell>
        <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
          <select
            id={`${row.id}`}
            value={row.weekStatus}
            onChange={async (event) => await handleAvailableChange(row.id, event)}
          >
            <option value="זמין">זמין</option>
            <option value="לא זמין">לא זמין</option>
          </select>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1 }}>
              <Typography variant="h6" gutterBottom component="div" sx={{ textAlign: 'right' }}>
                פרטים
              </Typography>
              <Table size="small" aria-label="details">
                <TableHead>
                  <TableRow>
                    <TableCell align="right">גודל משפחה</TableCell>
                    <TableCell align="right">העדפות אוכל</TableCell>
                    <TableCell align="right">הערות</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {row.details.map((detail, index) => (
                    <TableRow key={index}>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{detail.familySize}</TableCell>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
                        <input 
                          id="foodPreference" 
                          value={foodPreference} 
                          onChange={(event) => handleFoodPreferenceChange(event, row.id)} 
                        />
                      </TableCell>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
                        <input 
                          id="notes" 
                          value={notes} 
                          onChange={(event) => handleNotesChange(event, row.id)}
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <LoadingButton  
                style={{cursor: 'pointer'}}
                onClick={async () => await handleSave(row.id)}
                disabled={!inputsValid}
              >
                שמור
              </LoadingButton>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  );
}

Row.propTypes = {
  row: PropTypes.shape({
    firstName: PropTypes.string.isRequired,
    lastName: PropTypes.string.isRequired,
    phoneNumber: PropTypes.string.isRequired,
    street: PropTypes.string,
    address: PropTypes.string.isRequired,
    weekStatus: PropTypes.string.isRequired,
    date: PropTypes.string.isRequired,
    details: PropTypes.arrayOf(
      PropTypes.shape({
        familySize: PropTypes.number.isRequired,
        foodPreference: PropTypes.string,
        notes: PropTypes.string,
      })
    ).isRequired,
  }).isRequired,
};

const NeederTrackingTable = ({ data }: { data: NeederTrackingModel[] }) => {
  const rows = data.map((item) => createData(item));
  
  return (
    <TableContainer style={{ height: '100vh', width: '100%', background: 'rgba(255, 255, 255, 0.8)' }}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell />
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>שם</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>טלפון</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>כתובת</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <Row key={row.id} row={row} />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default NeederTrackingTable;