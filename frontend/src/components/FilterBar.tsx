import { Box, FormControl, InputLabel, MenuItem, Select } from '@mui/material'
import type { SelectChangeEvent } from '@mui/material'
import type { FilterOptions } from '@/types/politician'

interface FilterBarProps {
  filters: FilterOptions
  state: string
  party: string
  onStateChange: (value: string) => void
  onPartyChange: (value: string) => void
}

export function FilterBar({ filters, state, party, onStateChange, onPartyChange }: FilterBarProps) {
  return (
    <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
      <FormControl size="small" sx={{ minWidth: 180 }}>
        <InputLabel>State</InputLabel>
        <Select
          value={state}
          label="State"
          onChange={(e: SelectChangeEvent) => onStateChange(e.target.value)}
        >
          <MenuItem value="">All states</MenuItem>
          {filters.states.map(s => (
            <MenuItem key={s} value={s}>{s}</MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl size="small" sx={{ minWidth: 180 }}>
        <InputLabel>Party</InputLabel>
        <Select
          value={party}
          label="Party"
          onChange={(e: SelectChangeEvent) => onPartyChange(e.target.value)}
        >
          <MenuItem value="">All parties</MenuItem>
          {filters.parties.map(p => (
            <MenuItem key={p} value={p}>{p}</MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  )
}
