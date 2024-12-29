import * as React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import { Visit } from '../models/Visit';

const OutlinedCard = ({ visit }: { visit: Visit }) => {
  return (
    <Box sx={{ minWidth: 50, marginBottom: '10px', fontSize: 9 }}>
      <Card variant="outlined">
        <CardContent>
          <Typography gutterBottom sx={{ color: 'text.primary', fontSize: 12 }}>
            {visit.firstName} {visit.lastName}
          </Typography>
          <Typography variant="h5" component="div" sx={{ color: 'text.secondary', fontSize: 10 }}>
            {visit.phoneNumber}
          </Typography>
          <Typography variant="body2" sx={{ color: 'text.secondary', fontSize: 10 }}>
            {visit.address}    
          </Typography>
          <Typography variant="body2" sx={{ color: 'text.secondary', fontSize: 10 }}>
          {'שעת הגעה מקסימלית: ' + visit.maxHour}
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default OutlinedCard;
