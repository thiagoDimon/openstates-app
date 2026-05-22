import { Box, FormControl, InputLabel, MenuItem, Select } from '@mui/material'
import type { SelectChangeEvent } from '@mui/material'
import { US_STATE_OPTIONS } from '@/types/usState'
import { US_PARTY_OPTIONS } from '@/types/usParty'

interface FilterBarProps {
  state: string
  party: string
  onStateChange: (value: string) => void
  onPartyChange: (value: string) => void
}

export function FilterBar({ state, party, onStateChange, onPartyChange }: FilterBarProps) {
  return (
    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
      <FormControl size="small" sx={{ minWidth: 120 }}>
        <InputLabel>State</InputLabel>
        <Select
          value={state}
          label="State"
          onChange={(e: SelectChangeEvent) => onStateChange(e.target.value)}
        >
          <MenuItem value="">Select a state</MenuItem>
          {US_STATE_OPTIONS.map(s => (
            <MenuItem key={s} value={s}>{s}</MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size="small" sx={{ minWidth: 160 }}>
        <InputLabel>Party</InputLabel>
        <Select
          value={party}
          label="Party"
          onChange={(e: SelectChangeEvent) => onPartyChange(e.target.value)}
        >
          <MenuItem value="">All parties</MenuItem>
          {US_PARTY_OPTIONS.map(p => (
            <MenuItem key={p} value={p}>{p}</MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  )
}
